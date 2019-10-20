/** TRANSACTIONS AND SIGNATURES
 * each transaction will carry certain amount of data
 * public key - address of the sender of funds
 * public key - address of the sender of funds
 * the value  - amount of funds to be transferred
 * inputs	  - references to previous transactions to prove sender has funds to send
 * output	  - amount of relevant addresses received in a transaction
 * cryptographic signature - proves the owner of the address sending transactions and data hasn't being changed
 * 
 * Signatures perform two important tasks :
 *  # allow only owner to spend their coins.
 *  # prevent others from tampering with their submitted transaction before new block is mined. (at the point of entry)
 * 
 * # Private key is used to sign the data and 
 * # the public key is can be used to verify its integrity.
 * 
 * 
 */
package com.dsec.chain;

import java.security.*;
import java.util.ArrayList;
/**
 * @author spegusess
 *
 */
public class Transaction {
	public String transactionId;		// hash of the transaction
	public PublicKey sender;		// sender's address/public key
	public PublicKey reciepient;		// reciepient's address/public key
	public float value;
	public byte[] signature;		// prevents anybody else from spending funds
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	
	private static int sequence = 0;	// a rough count of how many transactions have been generated
	
	//constructor
	public Transaction(PublicKey from, PublicKey to, float value , ArrayList<TransactionInput>inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
		
	}
/*
    Transaction(PublicKey publicKey, PublicKey publicKey0, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/
        // This calculates the transaction hash (which will be used as its ID)
	private String calaculateHash() {
		sequence++; // Include the sequence to avoid 2 identical transactions having the same hash.
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence 
				);
	}
	// In reality there need to be sign more information like. inputs, outputs, time-stamps
	// sign all the data we don't ish to be tempered with
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
		
	}
	
	// verifies the data we sign hasn't been tempered with
	// signatures will be verified by miners as a new transactions are added to a block
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) +  Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

        // Returns true if new transaction could be created
        public boolean processTransaction(){
            if(verifySignature()==false){
                System.out.println("#Transaction Signature failed to verify");
                return false;
            }
            
            // gather transaction inputs (Make sure they are unspent)
            for(TransactionInput i : inputs){
                i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
            }
            
            // check if transaction is valid
            if(getInputsValue() < BlockChain.minimumTransaction){
                System.out.println("#Transaction Inputs to small : "+getInputsValue());
                return false;
            }
            
            // generate transaction outputs
            float leftOver =  getInputsValue() - value; // get value of inputs then the left over change
            transactionId = calculateHash();
            outputs.add(new TransactionOutput(this.reciepient, value,transactionId));   //send value to recipient
            outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));   //send the left over 'change' back to sender
            
            // add outputs to transaction list
            for(TransactionOutput o : outputs){
                BlockChain.UTXOs.put(o.id, o);
            }
            
            // remove transaction inputs from UTXO lists as spent
            for(TransactionInput i : inputs){
                if(i.UTXO == null) continue;    // if ransaction can't be fond skip it
                BlockChain.UTXOs.remove(i.UTXO.id);
                
            }
            return true;
        }
        
        // return sum of inputs (UTXOs values)
        public float getInputsValue(){
            float total = 0;
            for(TransactionInput i : inputs){
                if(i.UTXO == null) continue;    // if transaction can't be found skip it
                total += i.UTXO.value;
            }
            return total;
        }
        
        // return sum of outputs 
        public float getOutputsValue(){
            float total = 0;
            for(TransactionOutput o: outputs){
                total += o.value;
            }
            return total;
        }
        
        private String calculateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
        
        // towards the end we discarder inputs from lists of UTXOs. 
        // meaning transaction output can only be used once as an input, hence the full value of the inputs must be used.
        // so the sender 'change' back to themselves.
}
