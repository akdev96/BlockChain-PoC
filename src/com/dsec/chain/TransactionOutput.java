/**
 * 
 */
package com.dsec.chain;

import java.security.PublicKey;
/**
 * @author spegusess
 *  Transaction outputs will show the final amount sent to each party from the transaction.
 *  These, when referenced as inputs in new transactions, act as proof that user have coins to send.
 */
public class TransactionOutput {
	public String id;
	public PublicKey reciepient;        // owner of the coins
	public float value;                 // amount of coins user own
	public String parentTransactionId;  // the id of the transaction this output was created in
	
        // Constructor
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
	} 
        
        // Check if coin belongs to the user
        public boolean isMine(PublicKey publicKey){
            return (publicKey == reciepient);
        }
}
