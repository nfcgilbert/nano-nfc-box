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

import java.math.BigInteger;
import java.util.ArrayList;

public final class Address {
	private final Account parent;
	private final int index;
	private final byte[] publicKey;
	private final byte[] privateKey;
	private final String address;
	
	private boolean isOpened;
	private String representative;
	private BigInteger totalBalance, rawBalance, rawPending;
	private final ArrayList<String> unpocketedTransactions = new ArrayList<String>();
	
	private String nextPow;
	
	public Address(Account parent, int index, byte[] pub, byte[] priv, String address,
			String representative, /*String nextPOW,*/ boolean isOpened) {
		this.parent = parent;
		this.index = index;
		this.publicKey = pub;
		this.privateKey = priv;
		this.address = address;
		this.representative = representative;
		/*this.nextPow = nextPOW;*/
		this.isOpened = isOpened;
	}
	
	@Override
	public String toString() {
		return this.address;
	}
	
	public Account getParent() {
		return this.parent;
	}

	public int getIndex() {
		return index;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public String getAddress() {
		return address;
	}
	
	public String getRepresentative() {
		return this.representative;
	}
	
	public void setRepresentative(String newRep) {
		this.representative = newRep;
	}
	
	public String getNextPOW() {
		return this.nextPow;
	}
	
	public void setNextPOW(String newPOW) {
		this.nextPow = newPOW;
	}
	
	public ArrayList<String> getUnpocketedTransactions() {
		return this.unpocketedTransactions;
	}
	
	public boolean getIsOpened() {
		return this.isOpened;
	}
	
	public void setIsOpened(boolean newState) {
		this.isOpened = newState;
	}
	
	public BigInteger getRawPending() {
		return this.rawPending;
	}
	
	public void setPending(BigInteger newBalance) {
		this.rawPending = newBalance;
	}
	
	public BigInteger getRawBalance() {
		return this.rawBalance;
	}
	
	public void setBalance(BigInteger newBalance) {
		this.rawBalance = newBalance;
	}
	
	public BigInteger getRawTotalBalance() {
		return this.totalBalance;
	}
	
	public void setTotalBalance(BigInteger newBalance) {
		this.totalBalance = newBalance;
	}
}
