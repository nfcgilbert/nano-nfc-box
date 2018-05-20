/*
 * This app was written by /u/FantasyGilbert and helpers.
 *
 * Some of the code is copied together from other projects such as mainly the Rain wallet by thehen101 and perhaps
 * others that I may have forgotten. I didn't mean to copy from any open source projects without giving credit but
 * I didn't always keep track of the source.
 *
 * This code is not (even close to) perfect, it's an example app to demonstrate the protocol of the Nano Blackbox, an NFC cashpoint that utilises Nano.
 *
 * So much thank to the Nano community and its coders.
 *
 * This app may have some unused code, I tried to comment it all out but not 100% sure.
 */

package com.example.newestnanoapp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class Account {
	public static final char[] ACCOUNT_MAP = "13456789abcdefghijkmnopqrstuwxyz".toCharArray();
	public static final HashMap<String, Character> ACCOUNT_CHAR_TABLE = 
			new HashMap<String, Character>();
	public static final HashMap<Character, String> ACCOUNT_BIN_TABLE = 
			new HashMap<Character, String>();
	private final String defaultRep;

	private final ArrayList<Address> addresses = new ArrayList<Address>();
	
	private final byte[] seed;
	
	static
	{
		for (int i = 0; i < ACCOUNT_MAP.length; i++) {
			String bin = Integer.toBinaryString(i);
			while (bin.length() < 5)
				bin = "0" + bin; //pad with 0
			ACCOUNT_CHAR_TABLE.put(bin, ACCOUNT_MAP[i]);
			ACCOUNT_BIN_TABLE.put(ACCOUNT_MAP[i], bin);
		}
	}
	/*
	public Account(byte[] seed, String defaultRep) {
		this(seed, defaultRep, null);
	}
	*/
	public Account(byte[] seed, String defaultRep, ArrayList<Boolean> addressIndex) {
		this.seed = seed;
		this.defaultRep = defaultRep;
		this.initGenerateAddresses(this.addresses, addressIndex);
	}
	
	private void initGenerateAddresses(ArrayList<Address> adds, ArrayList<Boolean> shouldGenIndex) {
		if (shouldGenIndex == null) {
			this.addAddress(0);
			return;
		}
		adds.clear();
		for (int i = 0; i < shouldGenIndex.size(); i++) {
			boolean shouldGen = shouldGenIndex.get(i);
			if (shouldGen) {
				this.addAddress(i);
			} else {
				this.addresses.add(i, null);
			}
		}
	}
	/*
	public ArrayList<Boolean> getShouldGenerateAddressIndex() {
		ArrayList<Boolean> shouldGenIndex = new ArrayList<Boolean>();
		for (int i = 0; i < this.addresses.size(); i++) {
			boolean shouldGen = this.addresses.get(i) != null;
			shouldGenIndex.add(i, shouldGen);
		}
		return shouldGenIndex;
	}*/
	
	public void addAddress(int index) {
		if (index >= this.addresses.size()) {
			this.addresses.add(index, this.getAddressForIndex(index));
			return;
		}
		if (this.addresses.get(index) == null)
			this.addresses.add(index, this.getAddressForIndex(index));
	}
	
	public void removeAddress(int index) {
		if (index >= this.addresses.size())
			return;
		this.addresses.remove(index);
		this.addresses.add(index, null);
	}
	/*
	public Address getAddressAtIndex(int index) {
		return this.addresses.get(index);
	}
	
	public boolean isAddressAtIndex(int index) {
		if (index >= this.addresses.size())
			return false;
		return this.addresses.get(index) != null;
	}*/
	/*
	public int getMaxAddressIndex() {
		return this.addresses.size();
	}
	*/
	/*
	public byte[] getSeed() {
		return this.seed;
	}
	*/
	/**
	 * Gets an address for a given index. The majority of the times this method
	 * will be called, index will be zero as thats what is used for the first
	 * address. If a second address is made, the index *should* be one, so a
	 * different Address is returned (but both are generated from the same
	 * account seed).
	 * 
	 * @param index The index to get the address from
	 * @return The Address corresponding to the given index, or null if the
	 * passed index was invalid
	 */
	private Address getAddressForIndex(int index) {
		//iirc the reference spec uses an unsigned int
		if (index < 0)
			return null;
		
		//if we have already generated an Address for this index, return it.
		
		if (index < this.addresses.size()) {
			Address a = this.addresses.get(index);
			if (a != null)
				return a;
		}
		
		//if not, generate an address for the given index and return it.
		final Blake2b blake2b = Blake2b.Digest.newInstance(32); //will return 32 bytes digest
		blake2b.update(this.seed); //add seed
		blake2b.update(ByteBuffer.allocate(4).putInt(index).array()); //and add index
		byte[] privateKey = blake2b.digest(); //digest 36 bytes into 32
		byte[] publicKey = ED25519.publickey(privateKey); //return the public key
		
		Address newAddress = new Address(
				this, index, publicKey, privateKey, this.publicKeyToXRBAddress(publicKey),
				this.defaultRep, true);
		
		//add the generated address to the address table (hashmap) //new - this is handled by other methods
		//this.accountAddresses.put(newAddress.getIndex(), newAddress);
		
		return newAddress; //and finally return the new address
	}
	
	public static byte[] addressToPublicKey(String address) {
		if (address.length() != 64)
			return null;
		if (!address.substring(0, 4).equals("xrb_"))
			return null;
		
		String pub = address.substring(4, address.length() - 8);
		String checksum = address.substring(address.length() - 8);
		
		String pubBin = "";
		for (int i = 0; i < pub.length(); i++) {
			pubBin += ACCOUNT_BIN_TABLE.get(pub.charAt(i));
		}
		pubBin = pubBin.substring(4);
		
		String checkBin = "";
		for (int i = 0; i < checksum.length(); i++) {
			checkBin += ACCOUNT_BIN_TABLE.get(checksum.charAt(i));
		}
		
		String hat = DataManipulationUtil.binaryToHex(checkBin);
		while (hat.length() < 10)
			hat = "0" + hat;
		
		byte[] checkHex = DataManipulationUtil.swapEndian(DataManipulationUtil.hexStringToByteArray(hat));
		
		
		String fallaciousalbatross = DataManipulationUtil.binaryToHex(pubBin);
		while (fallaciousalbatross.length() < 64)
			fallaciousalbatross = "0" + fallaciousalbatross;
		
		byte[] publicKey = DataManipulationUtil.hexStringToByteArray(fallaciousalbatross);

		final Blake2b blake = Blake2b.Digest.newInstance(5);
		blake.update(publicKey);
		byte[] digest = blake.digest();
		if (Arrays.equals(digest, checkHex)) 
			return publicKey;
		
		return null;
	}
	
	/**
	 * Derives and returns an XRB address from a passed public key.
	 * 
	 * @param // publicK Public key to be used in address derivation.
	 * @return An XRB address.
	 */
	public static String publicKeyToXRBAddress(byte[] publicKey) {
		String keyBinary = DataManipulationUtil.hexToBinary(DataManipulationUtil.bytesToHex(publicKey)); //we get the address by picking
		//five bit (not byte!) chunks of the public key (in binary)
		
		final Blake2b blake2b = Blake2b.Digest.newInstance(5);
		blake2b.update(publicKey); //the blake2b digest will be used for the checksum
		byte[] digest = DataManipulationUtil.swapEndian(blake2b.digest()); //the original wallet flips it
		String bin = DataManipulationUtil.hexToBinary(DataManipulationUtil.bytesToHex(digest)); //we get the checksum by, similarly
		//to getting the address, picking 5 bit chunks of the five byte digest
		
		//calculate the checksum:
		String checksum = ""; //string that we will populate with the checksum chars
		while (bin.length() < digest.length * 8)
			bin = "0" + bin; //leading zeroes are sometimes omitted (idk why)
		for (int i = 0; i < ((digest.length * 8) / 5); i++) {
			String fiveBit = bin.substring(i * 5, (i * 5) + 5);
			checksum += ACCOUNT_CHAR_TABLE.get(fiveBit);//go through the [40] bits in
			//our digest and turn each five into a char using the accountCharTable
		}
		
		//calculate the address
		String account = ""; //string to populate with address chars
		while (keyBinary.length() < 260) //binary for address should always be 260 bits
			keyBinary = "0" + keyBinary; //so pad it if it isn't
		for (int i = 0; i < keyBinary.length(); i += 5) {
			String fiveBit = keyBinary.substring(i, i + 5);
			account += ACCOUNT_CHAR_TABLE.get(fiveBit); //go through the 260 bits that
			//represent our public key five bits at a time and convert each five bits
			//into a char that is retrieved from the accountCharTable
		}
		
		//return the address prefixed with xrb_ and suffixed with the checksum
		return "xrb_" + account + checksum;
	}
}
