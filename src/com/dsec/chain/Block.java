package com.dsec.chain;

/**
 * On the bitcoin network nodes share their blockchains and the longest valid chain is accepted by the network.
 * Proof of Work - stops tempering with data in an old block then creating a whole new longer blockchain.  
 * and the hashcash proof of work system means, it takes considerable time & computational power to create new blocks.
 * Hence the attacker would need more computational power than the rest of peers combined.
 */
import java.util.Date;
import java.util.ArrayList;

/**
 * @author spegusess
 */
public class Block {
	
	public String hash;					// holds digital signature
	public String previousHash;
        
        public String merkleRoot;
        public ArrayList<Transaction> transactions = new ArrayList<Transaction>();  // our data will be a simple message
        
	//private String data;
	public long timeStamp;					// as number of milliseconds since 1/1/1970
	public int nonce;					// to be included in calculateHash() 

	
	/** BLOCK CONSTRUCTOR
	 * @param data, previousHash
	 */
	public Block (/*String data,*/ String previousHash) {
		//this.data = data;             // String data no longer need to be paramaeter of a constructor
                
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash();		// do this after setting other values

	}
	
	/** calculate new hash based on block's contents
	* @return calculatedhash
	*/
	public String calculateHash() {
		String calculatedhash = StringUtil.applySha256(
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) +	//
				merkleRoot
                                // + data
				);
		return calculatedhash;
	}
	
	/** MINER
	 * miner is requires to do proof-of work
	 * by trying different variable values in the block untill it's hash starts with a certain number of 0's.
	 * 
	 * In reality each miner will start iterating from a random point. Some miners may even try random numbers for nonce.
	 * Harder difficulties solutions may even require more than integer.
	 * MAX_VALUE , miners can try changing the time stamp.
	 * 
	 * @param difficulty		// number of 0's they must solve for. suggests something around 6
	 * Currently LiteCoin's difficulty is around 443k
	 */
        
        //Increse nonce value until hash target is reached
	public void mineBlock(int difficulty) {
		//String target = new String(new char[difficulty]).replace('\0', '0');	// create a string with difficulty*"0"
                merkleRoot = StringUtil.gerMerkleRoot(transactions);
                String target = StringUtil.getDificultyString(difficulty);  // create a string with difficulty *"0"
		while(!hash.substring(0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
        
        // add transactions to this block
        // this will add the transactions and will only return true if the transaction has been successfully added.
        public boolean addTransaction(Transaction transaction){
            // process transaction and check if valid, unless block is genesis block then, ignore
            if(transaction == null) return false;
            if((previousHash != "0")){
                if((transaction.processTransaction() != true)){
                    System.out.println("Transaction Failed to Process. Discarded.");
                    return false;
                }
            }
            transactions.add(transaction);
            System.out.println("Transaction Successfully added to Block");
            return true;
        }

}
