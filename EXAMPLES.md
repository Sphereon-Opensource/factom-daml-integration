# Example Contracts
## Sending FAT Tokens with Approval
A common situation in a company is that when payments above a certain amount are made, they need to be approved by some approving party. This example contract enforces that logic.

*Note* - this guide is meant to be completed after all following all steps in the [README](README.md). To run this example contract, you will need to be running Mithra (both `client` and `operator`) as well as the DAML Sandbox using the example scenario as described below.

To set up the example scenario in the DAML Ledger, first install Mithra as described in the README, and run the DAML Sandbox using:
```bash
daml sandbox --scenario Main:example target/daml/mithra.dar
```
This scenario assigns the user Alice as an operator, and Charlie as a user. From there, Charlie can create a `SendWithApproval` contract for Bob that allows him to send FAT Tokens, but requires the approval of another party if the transaction amount is greater than a specified maximum.

To create the `SendWithApproval` contract:
* use the DAML Navigator to log in as Charlie
* under the `FAT.Onboarding:User` contract, select the option for `User_Create_SendWithApproval`. Fill in the fields:
    * `owner` - this must be Charlie as he cannot create a contract under someone else's name
    * `payer` - in this case the payer is Bob as he does not yet have any authorization to send FAT Tokens
    * `approver` - this can be anyone who must approve transactions when they are above the specified amount, either Charlie or Alice
    * `tokenId` - the id of the token that Bob is being authorized to send
    * `maxAmountWithoutApproval` - the maximum number of tokens that Bob can send without approval

Once the contract has been created, log in as Bob to see the `Examples.SendWithApproval:SendWithApproval` contract. This contract has a `Make_Payment` option with the usual `to`, `from` and `amount` fields. As explained in the [README](README.md), the `from` field has to match the secret address used by Bob's signing bot. If the amount field is less than `maxAmountWithoutApproval`, the transaction will be automatically processed by Mithra, otherwise, a `Examples.SendWithApproval:UnapprovedTransferRequest` contract will be created, and when approved by the `approver`, the transaction will be processed.
