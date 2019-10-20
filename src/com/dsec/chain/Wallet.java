/**
 * participants have an address which funds can be sent to and from
 * generate public key and private key in key pair using Elliptic Curve Cryptography
 * 
 * ### HOW CRYPTO CURRENCY IS OWNED.
 *      # to own 1 bitcoin, have to receive 1 bitcoin. the ledger doesn't add one bitcoin and minus 1 bitcoin from the sender.
 *        the sender reference that he previously received one bitcoin. then a transaction output was created showing that 1 bitcoin was sent to receiver's address.
 *        (Transaction inputs are references to previous transaction outputs)
 * 
 * ### Wallet balance is the sum of the all unspent transaction outputs addressed to user.
 * 
 * UTO : Unspent Transaction Outputs
 * TX  : Transaction
 * 
 * wallets are updated and gathers balance by looping through the UTXOs list and checking if a transaction output isMine();
 * 
 */
package com.dsec.chain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author spegusess
 */
public class Wallet {
	public PrivateKey privateKey;		//Used to sign transaction. have to keep hidden and secret
	public PublicKey publicKey;			//Act as wallet address, which is ok to share with others to receive payments
	
        public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();    // only UTXOs owned by this wallet
        
	public Wallet() {
		generateKeyPair();
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);		// 256 bytes provides an acceptable security level
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the KeyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
			
			
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		
	}
        
        //returns balance and store the UTXO's owned by this wallet in this.UTXOs
        public float getBalance(){
            float total = 0;
           for(Map.Entry<String, TransactionOutput> item : BlockChain.UTXOs.entrySet()){
               TransactionOutput UTXO = item.getValue();
               if(UTXO.isMine(publicKey)){   //if output(coins) belongs to me
                    UTXOs.put(UTXO.id, UTXO);  // add it to list of unspent transactions
                    total += UTXO.value;               
               } 
           }
           return total;
        } 
        
        // generates and returns a new transaction form this wallet.
        public Transaction sendFunds(PublicKey _recipient, float value){
            if(getBalance() < value){   // gather balance and check funds
                System.out.println("#Not Enough funds to send transaction. Transaction Discarded");
                return null;
            }
            // create arra list of inputs
            ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
            float total =0;
            for(Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()){
                TransactionOutput UTXO = item.getValue();
                total += UTXO.value;
                inputs.add(new TransactionInput(UTXO.id));
                if(total > value) break;
            }
            
            Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
            newTransaction.generateSignature(privateKey);
            for(TransactionInput input : inputs){
                UTXOs.remove(input.transactionOutputId);
            }
            return newTransaction;
        }
        
        // transaction history function to be coded
        
}
