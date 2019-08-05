# Mithra: DAML Library + Java App for DAML Integration

Mithra makes use of the DAML Ledger Java bindings in order to integrate the sending and receiving of FAT tokens within DAML contracts. This repo includes a set of automation bots that connect to the DAML Ledger, and a set of DAML contracts that provide templates for sending and receiving FAT Tokens. These templates can be used within, or as consequences of actions in any DAML contract.

## Installation and Usage
#### Dependencies
1. DAML SDK: to run Mithra, the DAML SDK must be installed on your system. Click [here](https://docs.daml.com/getting-started/installation.html) for the official installation instructions. This project uses DAML SDK 0.12.24. If you download the latest version, you can manually install 0.12.24 using the `daml install` command. Otherwise it should automatically download upon running `daml build ...`.

2. Maven: The Java app is a [Maven](https://maven.apache.org/) project. You will also need to have that installed in order to download all dependencies and execute the various targets.

3. Factom Dependencies:
  * [factomd, factom-walletd](https://github.com/FactomProject/distribution) - Be sure to install the Factom Command Line Interface Programs from the linked github.
  * [fatd](https://github.com/Factom-Asset-Tokens/fatd) - The Java app connects to fatd in order to submit transactions to the Factom blockchain. Installing fatd will also install the fat-cli for interacting with fatd. For this project, version for fatd/fat-cli is:
  ```bash
$ fat-cli --version
fat-cli:  v0.6.0.r0.g00ba028!
fatd:     v0.6.0.r0.g00ba028!
fatd API: 1
```
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
The first time you run this script, it will create a new Entry Credit address on the local testnet, fund it with ECs, and save it to `entry_credit_address.sh` in order for it to be used in the other scripts. Later when you run this script, it will look in this file and fund transactions on fatd using this address.

Once you have fatd, factomd, and factom-walletd running, you can run the `issue-tokens.sh` script, which will go through the process of creating a new FAT-0 token using fatd. 
```bash
./issue-tokens.sh /path/to/fatd/directory TOKEN_ID TOKEN_SYMBOL SUPPLY
```
The `TOKEN_ID` is the name of the token you want to create, the `TOKEN_SYMBOL` is a 1-4 letter code for identifying the token (for example, the token symbol for Factoids is FCT), and `SUPPLY` is the maximum amount of tokens that can be created. For an unlimited supply, you can use `-1`.

This script will also create a new Factom server identity using the `create-server-identity.sh` script, and store the output in a new directory `/serveridentity`. This may take some time as the script will need to wait to ensure that the identity has been registered.

Once the script is finished, it will output a `TOKEN_ID.json` file, which will need to be placed in `.../src/main/resources/tokens` in order to be used by the Mithra application. Additionally, the script will print out newly created Factoid addresses which have been funded with the newly created token. These addresses and keys can be used to demo Mithra, or new addresses can be created using factomd.

To kill the factomd, fatd, and factom-walletd instances started with `start-factomd-factomwalletd-fatd.sh`, you can use:
```bash
./kill-factomd-factomwalletd-fatd.sh
```
If there you want to clear all data on your local Factom testnet (this will also delete any FAT Tokens that have been locally initialized), you can use:
```bash
./remove-fat-state.sh 
```

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
##### 1. Starting DAML and fatd
In order to run Mithra, the following needs to be running:
* fatd
* factomd
* DAML Sandbox
* DAML Navigator

For running fatd, and factomd, we can run the same script as earlier, `start-factomd-factomwalletd-fatd.sh`, found in `/token-init`. To run the DAML Sandbox and DAML Navigator use:
```bash
daml sandbox target/daml/mithra.dar &
daml navigator server &
```

##### 2. Configuring Mithra

Before running Mithra, make sure that the `pom.xml` is configured to access fatd and the DAML Ledger. The default values are listed below. In addition, if you want to send FAT Tokens from a Factoid address, Mithra needs to use a secret key to sign transactions. For now, this is in the `pom.xml`, but in later versions will be moved to a keyvault for security.
```xml
    <properties>
        ...
        <ledgerHost>localhost</ledgerHost>
        <ledgerPort>6865</ledgerPort>
        <operatorParty>Alice</operatorParty>
        <clientParty>Bob</clientParty>
        <fatdEndpoint>http://localhost:8078</fatdEndpoint>
        <secretAddress>insert-secret-address-here</secretAddress>
        ...
    </properties>
```
'
##### 3. Starting the App
Mithra is a Spring Boot application with two different profile options. The `operator` profile models an admin user that can invite users to be able to send FAT Tokens using the `Onboarding` contracts. Once a user is onboarded, they can create `TransferRequest` contracts which are then processed by the operator app and sent to `fatd` once signed. By default in this example, Bob is designated as a client and Alice as an operator. More about how to use the roles is described in the next section. To run the app as `operator` use the following command:

```bash
mvn run spring-boot:run -Dspring.profiles.active=operator
```
Running the app as `client` allows for the signing of transactions using the secret address provided in the `pom.xml`. To run the app as `client` use the following:
```bash
mvn run spring-boot:run -Dspring.profiles.active=client
```
Note that in order to send transactions using DAML contracts, both the `client` and `operator` apps must be running.

#### Using Mithra
Once you have built Mithra and are running both the `client` and `operator` apps, you can open the DAML Navigator to interact with contracts as either Bob or Alice. The Navigator by default is running on <http://localhost:4000> In the example model, Alice is the operator and must invite Bob to be a User before he can initiate transactions.
##### To Onboard Bob
1. Under the dropdown at <http://localhost:4000>, chose Alice
2. Under the "Templates" tab select the contract starting with "FAT.Onboarding:UserInvitation@"
3. In the template, type "Alice" as the operator and "Bob" as the user and press submit. 
4. If the contract creation was successful, a check mark will briefly appear in the top right, and the contract will now be visible under the "Contracts" tab.

##### To Send a Transaction
1. Before Bob can send a transaction, he must accept the UserInvitation. Change users to Bob by clicking the user icon on the top left.
2. Under the "Contracts" tab, click on the "FAT.Onboarding:UserInvitation@" contract.
3. On the top bar, next to the contract number, chose "UserInvitation_Accept" and press submit
4. If this is successful there should be a contract "FAT.Onboarding:User@" available under the "Contracts" tab. By clicking on this contract, the "User_Send_FAT_Token" option will be available at the top bar.
5. Selecting the "User_Send_FAT_Token" option will prompt for the fields: "to", "from", "value", and "tokenId". By filling in these fields and pressing Submit, the transaction will be queued to send.
6. Looking under the "FAT Token Transfers" tab, the contract should appear as "FAT.Transfer:SignedTransactionTransfer". Once selected, there should be an option for "SignedTransactionTransfer_Send". Selecting and submitting this will send the FAT Transaction.
7. Sent transactions will be present under the "FAT Token Transfers" tab.
