# Mithra: DAML Library + Java App for DAML Integration

Mithra makes use of the DAML Ledger Java bindings in order to integrate the sending and receiving of FAT tokens within DAML contracts. This repo includes a set of automation bots that connect to the DAML Ledger, and a set of DAML contracts that provide templates for sending and receiving FAT Tokens. These templates can be used within, or as consequences of actions in any DAML contract.

## Installation and Usage
#### Dependencies
1. DAML SDK: to run Mithra, the DAML SDK must be installed on your system. Click [here](https://docs.daml.com/getting-started/installation.html) for the official installation instructions.

2. Maven: The Java app is a [Maven](https://maven.apache.org/) project. You will also need to have that installed in order to download all dependencies and execute the various targets.

3. Factom Dependencies:
  * [factomd, factom-walletd](https://github.com/FactomProject/distribution) - Be sure to install the Factom Command Line Interface Programs from the linked github.
  * [fatd](https://github.com/Factom-Asset-Tokens/fatd) - The Java app connects to fatd in order to submit transactions to the Factom blockchain. Installing fatd will also install the fat-cli for interacting with fatd.
  * [serveridentity](https://github.com/FactomProject/serveridentity) - An application that creates and manages a Factom Server's identity.
  
#### Creating a new FAT-0 Token
The scripts included under `/token-init` walk through the process of creating and initializing a new token using the FAT daemon. Please run the scripts within the `.../mithra/token-init/` directory as the scripts depend on each other.
```bash
git clone https://github.com/Sphereon/mithra.git
cd mithra/token-init
```

If any scripts won't run, you may need to make the script executable using:
```bash
chmod +x {name-of-script}.sh
```
or you can run them using `bash`
```bash
bash {name-of-script}.sh
```

These scripts, and Mithra in general, are not intended to be used in production as secret keys are not handled with adequate security. Therefore, it is highly recommended that these scripts be run with a local Factom net. To configure this, change the following variables in your `~/.factom/m2/factomd.conf`
```bash
DirectoryBlockInSeconds   = 3
...
Network                   = LOCAL
...
NodeMode                  = SERVER
```
Once you have factomd, factom-walletd, and fatd installed and configured on your system, you can start all of them using
```bash
./start-factomd-factomwalletd-fatd.sh /path/to/fatd/directory
```
The first time you run this script, it will create a new Entry Credit address on the local test-net, fund it with ECs, and save it to `entry_credit_address.sh` in order for it to be used in the other scripts. Later when you run this script, it will look in this file and fund transactions on fatd using this address.

Once you have fatd, factomd, and factom-walletd running, you can run the `issue-tokens.sh` script, which will go through the process of creating a new FAT-0 token using fatd. 
```bash
./issue-tokens.sh /path/to/fatd/directory TOKEN_ID TOKEN_SYMBOL SUPPLY
```
The `TOKEN_ID` is the name of the token you want to create, the `TOKEN_SYMBOL` is a 1-4 letter code for identifying the token (for example, the token symbol for Factoids is FCT), and `SUPPLY` is the maximum amount of tokens that can be created. For an unlimited supply, you can use `-1`.

This script will also create a new Factom server identity using the `create-server-identity.sh` script, and store the output in a new directory `/serveridentity`. This may take some time as the script will need to wait to ensure that the identity has been registered.

Once the script is finished, it will output a `TOKEN_ID.json` file, which will need to be placed in `.../src/main/resources/tokens` in order to be used by the Mithra application. Additionally, the script will print out newly created Factoid addresses which have been funded with the newly created token. These can be 

#### Building Mithra
Mithra uses the DAML-codegen to generate the Java code from DAML files that is necessary for the application. Therefore the DAML contracts must be compiled before the Java code. From the Mithra directory run:
```bash
daml build -o target/daml/mithra.dar
```
Now you should be able to compile Mithra:
```bash
mvn compile
```
#### Running Mithra
Mithra is a Spring Boot application with two different
