#!/bin/bash

source environment-assertions.sh

if [[ ! ("$#" == 1) ]]; then
    echo "Usage: $0 /path/to/fatd-folder"
    exit 1
fi

PATH_FATD_ROOT=$1
PATH_FATD=$PATH_FATD_ROOT/fatd
PATH_FATD_DB=./fatd.db

FACTOMD="factomd"
WALLETD="factom-walletd"
FACTOM_CLI="factom-cli"

assertFilePresent $PATH_FATD "FAT daemon"
assertCommandAvailable $FACTOMD
assertCommandAvailable $WALLETD

mkdir logs -p
mkdir $PATH_FATD_DB -p



echo "Starting $FACTOMD..."
nohup $FACTOMD &>logs/factomd.log &
sleep 5



echo "Starting $WALLETD..."
nohup $WALLETD &>logs/factom-walletd.log &
sleep 5

echo "Wait for Factom daemon HTTP API to be available..."
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8090)" != "200" ]]; do sleep 1; done
echo "Factom daemon HTTP API is available"

# First create an entry credit address to fund our FAT transactions
ESADR_FILE=./entry_credit_address.sh
if test -f "$ESADR_FILE"; then
    source ${ESADR_FILE}
else
    echo "------------------Entry credit addresses------------------"
    # Create an EC(Entry Credits) address from which to fund transactions. The EC address will be funded from the fixed sandbox Factoid address
    ecaddress=$($FACTOM_CLI newecaddress)
    # Retrieve the private key / secret for the EC address
    pkecaddress=$($FACTOM_CLI exportaddresses | grep $ecaddress | awk '{print $1}')
    echo "Newly created entry credit address: "$ecaddress
    echo "Newly created entry credit address private key: "$pkecaddress

    # Import address that has Factoids in Sandbox mode
    sourceFactoidAddress=$($FACTOM_CLI importaddress Fs3E9gV6DXsYzf7Fqx1fVBQPQXV695eP3k5XbmHEZVRLkMdD9qCK)
    echo "Existing Factoid address with factoids: "$sourceFactoidAddress

    # Fund our EC address
    $FACTOM_CLI buyec $sourceFactoidAddress $ecaddress 10000
    echo "Bought entrycredits from: ${sourceFactoidAddress}"
    echo "#!/bin/bash" >> ${ESADR_FILE}
    echo "pkecaddress=$pkecaddress" >> ${ESADR_FILE}
    echo "ecaddress=$ecaddress" >> ${ESADR_FILE}
fi

echo "Starting FATd..."
$PATH_FATD -debug -s http://localhost:8088 -esadr=${pkecaddress} -startscanheight 0 -dbpath $PATH_FATD_DB &>logs/fatd.log &
disown

sleep 1

echo "Waiting for FAT RPC API to be available..."
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8078)" != "200" ]]; do sleep 1; done
echo "FAT RPC API is available"

# Newline
echo ""
