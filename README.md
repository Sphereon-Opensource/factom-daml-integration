#Mithra: DAML Library + Java App for DAML Integration

Mithra makes use of the DAML Ledger Java bindings in order to integrate the sending and receiving of FAT tokens within DAML contracts. This repo includes a set of automation bots that connect to the DAML Ledger, and a set of DAML contracts that provide templates for 

##Installation and Usage
####Dependencies
1. DAML SDK: to run Mithra, the DAML SDK must be installed on your system. Click [here](https://docs.daml.com/getting-started/installation.html) for the official installation instructions.

2. Maven: The java app is a [Maven](https://maven.apache.org/) project. You will also need to have that installed in order to download all dependencies and execute the various targets.

3. Factom Dependencies:
  * [factomd, factom-walletd](https://github.com/FactomProject/distribution) - Be sure to install the Factom Command Line Interface Programs from the linked github
  * [fatd](https://github.com/Factom-Asset-Tokens/fatd) - The Java app connects to fatd in order to submit transactions to the Factom blockchain. Installing fatd will also install the fat-cli for interacting with fatd
  
####Creating a new FAT-0 Token
The scripts included under /token-init walk through the process of creating and initializing a new token using the FAT daemon. These scripts, and Mithra in general, are not intended to be used in production as secret keys are not handled with adequate security. Therefore, it is highly recommended that these scripts be run with a local Factom net. To configure this, change the following variables in your `~/.factom/m2/factomd.conf`
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
The first time you run this script, it will create a new Entry Credit address


