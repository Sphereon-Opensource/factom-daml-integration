#!/bin/bash

source environment-assertions.sh

PATH_FATD_DB=./fatd.db

echo "Removing databases: Factom, Factom-wallet and FATD. Press [Y] to continue or [N] to abort..."
if asksure; then
    echo "Removing..."
    rm -rf ~/.factom/m2/local-database
    rm -rf ~/.factom/wallet
    rm -rf $PATH_FATD_DB
    rm -rf ./serveridentity
    rm entry_credit_address.sh
    echo "Done"
else
    echo "Did not remove"
fi
