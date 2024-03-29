/**
 * 
 */
package com.dsec.chain;

/**
 * @author spegusess
 * This will be used to reference TransactionOutputs that have not yet been spent.
 * The transactionOutputId will be used to find the relevant TransactionOutput, 
 * allowing miners to check ownership.
 */
public class TransactionInput {
	public String transactionOutputId;		// Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO;			// Contains the Unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
