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

public final class DataManipulationUtil {
	/**
	 * An array of hex characters used by the {@link #bytesToHex(byte[])}
	 * method.
	 */
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	/**
	 * Converts an array of bytes into their String representation.
	 * Note that there are no "0x" prefixes, just the value of the
	 * byte is included in the string. For example, if you passed a
	 * byte array containing two bytes, [0xCA, 0xFE], the returned
	 * string would be "CAFE". Bytes in the string will always be
	 * uppercase.
	 * 
	 * @param bytes An array of bytes to be represented as a string.
	 * @return A string representation of the passed byte array.
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	/**
	 * Returns a string containing an array of bytes as a byte array.
	 * This inverses the {@link #bytesToHex(byte[])} method - if a
	 * string containing "CAFEBABE" is passed to this method, it will
	 * return a byte array containing: 0xCA, 0xFE, 0xBA, 0xBE.
	 * 
	 * @param s A string containing hex bytes.
	 * @return A byte array containing the bytes of a passed string.
	 */
	public static byte[] hexStringToByteArray(String s) {

		if (s.length() % 2 == 1)
		{
			s = "0" + s;
			//throw new Exception("The binary key cannot have an odd number of digits");
		}

	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/**
	 * Swaps the endianness of a byte array. This is achieved by 
	 * reversing the byte array, the original array is not changed.
	 * 
	 * @param b The byte array to have the order reversed.
	 * @return The passed byte array, but reversed.
	 */
	public static byte[] swapEndian(byte[] b) {
		byte[] bb = new byte[b.length];
		for (int i = b.length; i > 0; i--) {
			bb[b.length - i] = b[i - 1];
		}
		return bb;
	}

	public static String nibble_ByteArrayToString(byte[] bytes)
	{
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<bytes.length; i++)
		{
			byte b = bytes[i];
			builder.append(String.format("%02X",b));
		}
		return builder.toString();
	}
	
	/**
	 * Converts a hex string into the appropriate binary values.
	 * The actual value of the string's bytes in memory are not 
	 * what is converted to binary - but rather the bytes that
	 * the string represents (contains). For example a string
	 * passed to this method containing "CAFE" would return a
	 * string containing "1100101011111110". However, it should 
	 * be noted that sometimes this method doesn't return all of
	 * the required leading zeroes, so you may want to do some
	 * validation yourself.
	 * 
	 * @param hex A string containing valid hex characters.
	 * @return A binary representation of the passed string.
	 */
	public static String hexToBinary(String hex) {
		String value = new BigInteger(hex, 16).toString(2);
		String formatPad = "%" + (hex.length() * 4) + "s";
		return (String.format(formatPad, value).replace(" ", ""));
	}
	
	public static String binaryToHex(String bin) {
		BigInteger b = new BigInteger(bin, 2);
		return b.toString(16).toUpperCase();
	}
}
