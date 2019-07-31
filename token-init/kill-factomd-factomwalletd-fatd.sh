#!/bin/bash

echo "Killing existing factomd & walletd processes...."
pgrep 'fatd' | xargs -r kill
pgrep 'factom-walletd' | xargs -r kill
pgrep 'factomd' | xargs -r kill
