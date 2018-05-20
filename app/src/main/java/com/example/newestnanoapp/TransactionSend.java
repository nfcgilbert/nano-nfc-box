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

import android.util.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class TransactionSend extends Transaction
{
    private final Address senderAddressDontUseUseStringInstead;
    private final String previous, destinationAddress, amountHex;
    public String signature = null;
    private String representative=null;
    private String appAccount=null;
    private final BigInteger amountLeftRaw;

    public TransactionSend(String previous, Address address, String sendToAddress,
                           BigInteger finalAmountLeft, String _representative, String _appAccount) {
        super(Transaction.Type.SEND);

        Log.w("TransactionSend", "constructed");
        Log.w("---previous---", previous);
        Log.w("---sendToAddress---", sendToAddress);
        Log.w("---finalAmountLeft---", ""+finalAmountLeft);
        Log.w("---_representative---", _representative);
        Log.w("---_appAccount---", _appAccount);


        this.senderAddressDontUseUseStringInstead = address;
        this.previous = previous;
        this.destinationAddress = sendToAddress;

        this.amountLeftRaw = finalAmountLeft;

        String raw = amountLeftRaw.toString(16).toUpperCase();
        while (raw.length() < 32)
            raw = "0" + raw;
        this.amountHex = raw;

        this.representative = _representative;
        this.appAccount = _appAccount;
        this.signature = calculateSignature();
    }

    public static final char[] ACCOUNT_MAP = "13456789abcdefghijkmnopqrstuwxyz".toCharArray();
    public static final HashMap<String, Character> ACCOUNT_CHAR_TABLE =
            new HashMap<String, Character>();
    public static final HashMap<Character, String> ACCOUNT_BIN_TABLE =
            new HashMap<Character, String>();


    static {
        //populate the ACCOUNT_CHAR_TABLE and ACCOUNT_BIN_TABLE
        for (int i = 0; i < ACCOUNT_MAP.length; i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < 5)
                bin = "0" + bin; //pad with 0
            ACCOUNT_CHAR_TABLE.put(bin, ACCOUNT_MAP[i]);
            ACCOUNT_BIN_TABLE.put(ACCOUNT_MAP[i], bin);
        }
    }

    public String getDestinationAddress() {
        return this.destinationAddress;
    }
    public BigInteger getSendAmount() {
        return this.amountLeftRaw;
    }

    static String STATE_BLOCK_PREAMBLE = "0000000000000000000000000000000000000000000000000000000000000006";
    @Override
    String calculateSignature()
    {
        final Blake2b blake = Blake2b.Digest.newInstance(32);
        blake.update(DataManipulationUtil.hexStringToByteArray(STATE_BLOCK_PREAMBLE)); // ok
        blake.update(addressToPublicKey(this.appAccount)); // ok
        blake.update(DataManipulationUtil.hexStringToByteArray(this.previous));  // ok
        blake.update(addressToPublicKey(this.representative)); // ok
        blake.update(DataManipulationUtil.hexStringToByteArray(leftPad(radix(amountLeftRaw),32))); // ??
        blake.update(addressToPublicKey(destinationAddress)); // ok

        byte[] digested = blake.digest();

        // This is noble. It signs using a C library called Monocypher. Verrrry fast.
        byte[] signature = MainActivity.thisMainActivity.signPublic(digested, CardService.HexStringToByteArray(AccountDataContainer.privateKey),
                addressToPublicKey(AccountDataContainer.account));

        return DataManipulationUtil.bytesToHex(signature);
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
/*
    public static String getBlockHash(String previous, String sender, BigInteger balance, String representative, String link)
    {
        return DataManipulationUtil.bytesToHex(createHash(previous, sender, balance, representative, link));
    }

    static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }*/

    static String radix(BigInteger value) {
        return leftPad(value.toString(16).toUpperCase(), 32);
    }

    public static String leftPad(String str, int size) {
        if (str.length() >= size) {
            return str;
        }

        StringBuilder builder = new StringBuilder();
        while (str.length() + builder.length() < size) {
            builder.append("0");
        }
        return builder.append(str).toString();
    }
    /*
    public static byte[] createHash(String previous, String sender, BigInteger amount, String representative, String destinationAddress)
    {
        final Blake2b blake = Blake2b.Digest.newInstance(32);
        blake.update(DataManipulationUtil.hexStringToByteArray(STATE_BLOCK_PREAMBLE));
        blake.update(addressToPublicKey(sender)); // ok
        blake.update(DataManipulationUtil.hexStringToByteArray(previous));
        blake.update(addressToPublicKey(representative));
        blake.update(DataManipulationUtil.hexStringToByteArray(leftPad(radix(amount),32)));
        blake.update(addressToPublicKey(destinationAddress));

        byte[] digested = blake.digest();
        return digested;
    }*/

    // Never actually used
    @Override
    public String getAsJSON()
    {
        return
                "{"+
                    "\"action\": \"process\","+
                    "\"block\":\"{\\\"type\\\":\\\"state\\\",\\\"account\\\":\\\""+appAccount+"\\\",\\\"previous\\\":\\\""+previous+"\\\",\\\"representative\\\":\\\""+representative+"\\\",\\\"balance\\\":\\\""+amountLeftRaw+"\\\",\\\"link\\\":\\\""+DataManipulationUtil.bytesToHex(addressToPublicKey(this.destinationAddress))+"\\\",\\\"link_as_account\\\":\\\""+this.destinationAddress+"\\\",\\\"signature\\\":\\\""+this.signature+"\\\",\\\"work\\\": \\\""+"###"+"\\\"}\"}";
    }
}