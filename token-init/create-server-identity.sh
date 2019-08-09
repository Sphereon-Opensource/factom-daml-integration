#!/bin/bash

if [[ ! ("$#" == 2) ]]; then
    echo "Usage: $0 /path/to/server-identity/folder primary-key-ec-address"
    exit 1
fi

SERVER_IDENTITY_FOLDER=$1
PRI_KEY_EC_ADDRESS=$2

mkdir -p $SERVER_IDENTITY_FOLDER
cd $SERVER_IDENTITY_FOLDER
# Generate a full server identity
serveridentity full elements $PRI_KEY_EC_ADDRESS -f > serveridentityoutput.txt
cd ..
