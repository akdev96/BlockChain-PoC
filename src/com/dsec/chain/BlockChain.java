/**
 * Basic BlockChain
 * 
 * This is a PoC (proof of concept) blockchain project i learnt from "Programmers BlockChain with kass" @ www.medium.com
 * Tutor : www.github.com/CryptoKass
 * 
 * Blockchain is a list of blocks. 
 * each blockchain will have it's own digital signature in SHA-256, 
 * and contains digital signature of the previous block, 
 * and have some data.
 * 
 * hash= digital signature
 * each block doesn't just contain the hash of the block before it,
 * but its own hash is in part, calculated from the previous hash.
 * if the previous block's data is changed then the previous block's hash will change.
 * calculating and comparing the hashes allow us to see if a blockchain is invalid.
 * 
 * changing any data in this list, will change the signature and brake the blockchain.
 * 
 * #####################################
 * There are many ways to create new coins:
 *      # miners can include a transaction to themselves as a reward for each block mined
 *      # for now will relese all the coins we wish to have in the genesis block
 *
 * create wallets with --> new Wallet()
 * wallets with public and private keys using Elliptic-Curve cryptography
 * secure the transfer funds by using a digital signature algorithms to prove ownership
 * allow users to make transactions with --> Block.addTransaction(walletA.sendFunds(walletB.publicKey,20f));
 */

package com.dsec.chain;

import java.security.Security;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;		// gson - helps to turn an object into json, and for peer2peer stuff
import java.util.Base64;
import java.util.HashMap;                       // hashmaps - to use a key to find a value.


/**
 * @author spegusess
 */
public class BlockChain {

        public static ArrayList<Block> blockchain = new ArrayList<Block>();
        // coz of blockchains might be long and have to use more process power to use
        // to avoid that keep an extra collection of all unspent transactions that can be used as inputs. Collection is implemented as below.
        public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // list all unspent transactions 
	
        public static int difficulty = 5;
        public static float minimumTransaction = 0.1f;
	
	public static Wallet walletA;
	public static Wallet walletB;
        
        public static Transaction genesisTransaction;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/** TESTING FIRST 3
		//very fist block is called genesis block
		Block genesisBlock = new Block("Hello Block Chains", "0"); 		// just add 0 becoz there is no previous hash
		System.out.println("Hash for Block 1 : "+ genesisBlock.hash);
		
		Block secondBlock  = new Block("Second Block in the Chain",genesisBlock.hash);
		System.out.println("Hash for Block 2 : "+ secondBlock.hash);
		
		Block thirdBlock   = new Block("Third Block is Here", secondBlock.hash);
		System.out.println("Hash for Block 3 : "+ thirdBlock.hash);
		*/
		
		/** ADD BLOCKS TO BLOCKCHAIN ARRAY
		 * Update the BlockChain class to trigger the mineBlock() method for each new block.
		 * The isChainValid() Boolean should also check if each block has solved(by mining) hash
		 */
	
		// set bouncy-castle as a security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		// create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
                Wallet coinbase = new Wallet();
                
                // create genesis transaction, which sends 100 BTC to WalletA
                genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
                genesisTransaction.generateSignature(coinbase.privateKey);   //manually sign the genesis transaction
                genesisTransaction.transactionId = "0";     // manually set the transaction Id
                genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); // manually add the transaction output
                UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));  //important to store first transaction in the UTXOs list.
                
                System.out.println("Creating and Mining Genesis Block ...");
                Block genesis = new Block("0");
                genesis.addTransaction(genesisTransaction);
                addBlock(genesis);
                
                //Testing
                Block block1 = new Block(genesis.hash);
                System.out.println("\nWalletA's balance is : "+walletA.getBalance());
                System.out.println("WalletA is attempting to send funds (40) to walletB ...");
                block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
                addBlock(block1);
                System.out.println("\nWalletA's balance is : "+walletA.getBalance());
                System.out.println("\nWalletB's balance is : "+walletB.getBalance());
                
                Block block2 = new Block(block1.hash);
                System.out.println("\nWalletA Attempting to send more funds (1000) than it has ...");
                block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
                addBlock(block2);
                System.out.println("\nWalletA's balance is : "+walletA.getBalance());
                System.out.println("\nWalletB's balance is : "+walletB.getBalance());
                
                Block block3 = new Block(block2.hash);
                System.out.println("\nWalletB is Attempting to send funds (20) to WalletA ...");
                block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
                System.out.println("\nWalletA's balance is : "+walletA.getBalance());
                System.out.println("\nWalletB's balance is : "+walletB.getBalance());
                
                isChainValid();
                
		// Test Public and private keys
		/*
                System.out.println("Private and Public Keys : ");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
                */
                
		// create a test transaction from walletA to walletB
                /*
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		// verify the signature works and verify it's from the public key 
		System.out.println("Is Signature Verified : ");
		System.out.println(transaction.verifySignature());
		*/
		
		//
                /*
		blockchain.add(new Block("Hello Block Chains", "0"));
		System.out.println("Trying to mine Block 1 ...");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block("Second Block in the Chain", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to mine Block 2 ...");
		blockchain.get(1).mineBlock(difficulty);
		
		
		blockchain.add(new Block("Third Block is Here", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to mine Block 3 ...");
		blockchain.get(2).mineBlock(difficulty);
		
		System.out.println("\nBlockChain is Valid : "+ isChainValid());
		*/
		/*		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe BlockChain : ");
		System.out.println(blockchainJson);
		*/
	}
	
	public static Boolean isChainValid() {
		/**
		 * Any changes made to the blockchain's blocks will cause this method to return false
		 * 
		 * If someone tempered with the data in blockchain system,
		 * 	# their blockchain would be invalid
		 * 	# they would not be able to create a longer blockchain.
		 * 	# Honest blockchain in the network will have a time advantage on the longest chain.
		 * 	# tempered blockchain will not be able to catch-up with a longer & valid chain.
		 * 	# unless they have vastly more computation speed than all other nodes in network combined.
		 */
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();    //temporary working lists of unspent transactions at a given block state.
                tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
                
		// loop through blockchain to check hashes
		for(int i=0; i<blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			// compare registered hash and calculated hash
			if(!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("# Current Hashes not equal");
				return false;
			}
			
			// compare previous hash and registerd previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("# Previous Hashes not equal");
				return false;
			}
			
			// check if hash is solved
			if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("# This block hasn't mined");
				return false;
			}
                        
                        //loop through blockchains transactions
                        TransactionOutput tempOutput;
                        for(int t=0; t< currentBlock.transactions.size(); t++){
                            Transaction currentTransaction = currentBlock.transactions.get(t);
                            
                            if(!currentTransaction.verifySignature()){
                                System.out.println("# Signature on Transaction(" + t + ") is invalid");
                                return false;
                            }
                            if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                                System.out.println("# Inputs are not equal to outputs on Transaction (" + t + ")");
                                return false;
                            }
                            for(TransactionInput input : currentTransaction.inputs){
                                tempOutput = tempUTXOs.get(input.transactionOutputId);
                                if(tempOutput == null){
                                    System.out.println("# Referenced input on Transaction (" + t + ") is Missing");
                                    return false;
                                }
                                if(input.UTXO.value != tempOutput.value){
                                    System.out.println("# Referenced input Transaction (" + t + ") value is Invalid");
                                    return false;
                                }
                                
                                tempUTXOs.remove(input.transactionOutputId);
                            }
                            
                            for(TransactionOutput output : currentTransaction.outputs){
                                tempUTXOs.put(output.id, output);
                            }
                            if(currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient){
                                System.out.println("# Transaction (" + t + ") output receipient is not who it should be");
                                return false;
                            }
                            if(currentTransaction.outputs.get(1).reciepient != currentTransaction.sender){
                                System.out.println("# Transaction (" + t + ") output 'change' is not sender ");
                                return false;
                            }
                        }
                        
		}
                
                System.out.println("BlockChain is Valid");
		return true;
	}
        
        public static void addBlock(Block newBlock){
            newBlock.mineBlock(difficulty);
            blockchain.add(newBlock);
        }
	
}

// in part 3 : peer2peer networking, consensus algorithms, block storage and databases