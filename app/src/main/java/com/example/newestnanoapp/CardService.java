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

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.math.BigInteger;
import java.util.Arrays;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.os.Message;
import android.util.Log;

/**
 * This is a sample APDU Service which demonstrates how to interface with the card emulation support
 * added in Android 4.4, KitKat.
 *
 * <p>This sample replies to any requests sent with the string "Hello World". In real-world
 * situations, you would need to modify this code to implement your desired communication
 * protocol.
 *
 * <p>This sample will be invoked for any terminals selecting AIDs of 0xF11111111, 0xF22222222, or
 * 0xF33333333. See src/main/res/xml/aid_list.xml for more details.
 *
 * <p class="note">Note: This is a low-level interface. Unlike the NdefMessage many developers
 * are familiar with for implementing Android Beam in apps, card emulation only provides a
 * byte-array based communication channel. It is left to developers to implement higher level
 * protocol support as needed.
 */
public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    // AID for our loyalty card service.
   // private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";

   // private static final String SELECT_APDU_HEADER = "13A47431";
    byte[] loginByteArray = new byte[] { (byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00, (byte)0x05, (byte)0xF2,
            (byte)0xA7, (byte)0x31, (byte)0xD8, (byte)0x7C}; // A731D87C
   // byte[] loginByteArray = new byte[] { (byte)0x13, (byte)0xA4, (byte)0x74, (byte)0x31, (byte)0x05, (byte)0xF2,
   //         (byte)0x22, (byte)0x22, (byte)0x22, (byte)0x22};

    // "OK" status word sent in response to SELECT AID command (0x9000)
   // private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
   // private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
   // private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);

    public static MainActivity mainAct;

    public static void setMainActivity(MainActivity _mainAct)
    {
        mainAct = _mainAct;
    }

    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    @Override
    public void onDeactivated(int reason) {
        Log.w("Deactivated", "Reason "+reason);

    }

    /**
     * This method will be called when a command APDU has been received from a remote device. A
     * response APDU can be provided directly by returning a byte-array in this method. In general
     * response APDUs must be sent as quickly as possible, given the fact that the user is likely
     * holding his device over an NFC reader when this method is called.
     *
     * <p class="note">If there are multiple services that have registered for the same AIDs in
     * their meta-data entry, you will only get called if the user has explicitly selected your
     * service, either as a default or just for the next tap.
     *
     * <p class="note">This method is running on the main thread of your application. If you
     * cannot return a response APDU immediately, return null and use the {@link
     * #sendResponseApdu(byte[])} method later.
     *
     * //@param commandApdu The APDU that received from the remote device
     * //@param extras A bundle containing extra data. May be null.
     * @return a byte-array containing the response APDU, or null if no response APDU can be sent
     * at this point.
     */

    /*
     * Unsigned send packet contains the new balance after the transaction as a hex number
     */
    /*
    public static BigInteger extractOldAmountRaw(String unsignedPacket)
    {
        int indexStart = unsignedPacket.indexOf(":ob")+4;
        String stringAfterBalance = unsignedPacket.substring(indexStart);
        int indexEnd = stringAfterBalance.indexOf(",");
        String amountString = stringAfterBalance.substring(0, indexEnd);
        //Log.i("amountString", amountString);
        BigInteger newRaw = new BigInteger(amountString, 10);
        return newRaw;
    }

    public static String extractPreviousBlockHash(String unsignedPacket)
    {
        int indexStart = unsignedPacket.indexOf(":pb")+4;
        String stringAfterBalance = unsignedPacket.substring(indexStart);
        int indexEnd = stringAfterBalance.indexOf(",");
        String blockHashString = stringAfterBalance.substring(0, indexEnd);
        return blockHashString;
    }

    public static String extractOwnWalletRepresentative(String unsignedPacket)
    {
        int indexStart = unsignedPacket.indexOf(":re")+4;
        String stringAfterBalance = unsignedPacket.substring(indexStart);
        int indexEnd = stringAfterBalance.indexOf(",");
        String blockHashString = stringAfterBalance.substring(0, indexEnd);
        //Log.w("EXTR", blockHashString);
        return blockHashString;
    }

    public static String extractDestinationAddress(String unsignedPacket)
    {
        int indexStart = unsignedPacket.indexOf(":da")+4;
        String stringAfterBalance = unsignedPacket.substring(indexStart);
        int indexEnd = stringAfterBalance.indexOf(",");
        String blockHashString = stringAfterBalance.substring(0, indexEnd);
        return blockHashString;
    }

    public static String extractSendAmount(String unsignedPacket)
    {
        int indexStart = unsignedPacket.indexOf(":sa")+4;
        String stringAfterBalance = unsignedPacket.substring(indexStart);
        int indexEnd = stringAfterBalance.indexOf("#");
        String sendAmount = stringAfterBalance.substring(0, indexEnd);
        Log.w("sendAmount", sendAmount);
        return sendAmount;
    }*/

    /*
     * returns true if 2nd byte is CA, otherwise false
     * rephrased: it returns false if this is the apdu login command, otherwise true
     */
    private boolean isMessage(byte[] inByte)
    {
        if(inByte[1]== (byte)0xCA)
            return true;
        else
            return false;
    }

    private byte[] getMessageBytes(byte[] inByte)
    {
        byte[] newArray = new byte[inByte.length-5];
        for(int i=5; i<inByte.length; i++)
        {
            newArray[i-5] = inByte[i];
        }
        return newArray;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >> 8),
                (byte)value};
    }

    public static byte[] signatureForBox = null;

    byte[] invoiceArray = null;
    int invoiceStartIndex = 3;
    int invoiceArrayCurrentIndex=0;
    int invoiceLength=0;

    public static ByteInvoice invoice=null;

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras)
    {
        byte[] messageBytes=null;
        byte[] returnBytes=new byte[] { (byte)0x90, (byte)0x00 };

        if(isMessage(commandApdu))
        {
            Log.w("Beep", "It's a message!");
            try
            {
                messageBytes = getMessageBytes(commandApdu);
                if((messageBytes!=null && messageBytes.length>0))
                    Log.w(TAG, "Received bytes: " + messageBytes.length);
                else
                    return new byte[] { (byte)0x90, (byte)0x00 };
            } catch (Exception ex) {
                Log.w("Error", "error", ex);
            }
        }

        if(messageBytes==null || messageBytes.length==0 || Arrays.equals(commandApdu, loginByteArray))
            return new byte[] { (byte)0x90, (byte)0x00 };

        try
        {
            if (messageBytes[0]==IncomingRequestHeaders.INCOMING_INVOICE_START)
            {
                try {
                    Log.w(TAG, "...Spotted invoice start...");
                    invoiceLength = ((messageBytes[1] & 0xff) << 8) | (messageBytes[2] & 0xff);
                    Log.w(TAG, "...invoiceLength = " + invoiceLength);
                    signatureForBox = null;
                    invoiceArray = new byte[invoiceLength];
                    invoiceStartIndex = 3;
                    invoiceArrayCurrentIndex = 0;
                    int bytesToCopyFromThisArray = 255 - invoiceStartIndex;
                    if (invoiceLength < bytesToCopyFromThisArray)
                        bytesToCopyFromThisArray = invoiceLength;
                    System.arraycopy(messageBytes, invoiceStartIndex, invoiceArray, 0, bytesToCopyFromThisArray);
                    invoiceArrayCurrentIndex += bytesToCopyFromThisArray;

                    //Log.w(TAG, "invoiceArray.length = " + invoiceArray.length);
                    //Log.w(TAG, "invoiceLength = " + invoiceLength);

                    if(invoiceArray.length == invoiceLength)
                    {
                        createInvoice();
                    }
                    returnBytes = intToByteArray(messageBytes.length);
                }
                catch(Exception e)
                {
                    Log.w(TAG, "Exception", e);
                    returnBytes = new byte[] { IncomingRequestHeaders.SEND_AGAIN };
                }
            }

            if(messageBytes.length==1 && messageBytes[0]==IncomingRequestHeaders.SIGNED_PACKET_RECEIVED_OK)
            {
                printMessage("You have authorized the payment.");
            }

            if (messageBytes[0]==IncomingRequestHeaders.INCOMING_INVOICE_FOLLOWUP)
            {
                Log.w(TAG, "...Spotted invoice followup...");
                int remainingBytes = invoiceArray.length - invoiceArrayCurrentIndex;
                System.arraycopy(messageBytes, 1, invoiceArray, invoiceArrayCurrentIndex, messageBytes.length-1);

                try
                {
                    if(invoiceArray.length == invoiceLength)
                    {
                        createInvoice();
                    }
                    returnBytes = intToByteArray(messageBytes.length);
                } catch (Exception e) {
                    Log.w("Error", "error", e);
                    returnBytes = new byte[] { IncomingRequestHeaders.SEND_AGAIN } ;
                }
            }

            if(messageBytes[0]==IncomingRequestHeaders.FINAL_RESULT_HEADER)
            {
                Log.w(TAG, "Got final result");
                byte messageLength = messageBytes[1];
                Log.w(TAG, "messageLength = " + messageLength);
                if(messageLength==messageBytes.length-2)
                {
                    byte[] finalResultBytes = new byte[messageBytes.length-2];
                    System.arraycopy(messageBytes, 2, finalResultBytes, 0, finalResultBytes.length);
                    String finalResult = new String(finalResultBytes);
                    String[] resultArr = finalResult.split(":");
                    switch(resultArr[0])
                    {
                        case "b":
                            printMessage("New balance: " + resultArr[1]);
                            break;
                        case "e":
                            printMessage("Error: "+resultArr[1]);
                            break;
                    }
                }
                else
                {
                    printMessage("Final result not complete. Payment may have gone through anyway.");
                }
            }

            if (messageBytes[0]==IncomingRequestHeaders.SIGNED_PACKET_REQUEST) // is a request for the user's decision where to pay or not
            {
                if (MainActivity.currentInvoice != null)
                {
                    if (MainActivity.currentInvoice.paymentDecisionStatus==IncomingRequestHeaders.PAYMENT_DECISION_PAY)
                    {
                        if(signatureForBox!=null)
                        {
                            returnBytes = signatureForBox;
                        }
                    }
                }
            }

            if(messageBytes[0] == IncomingRequestHeaders.ACCOUNT_REQUEST) // is a request for the user's account to the packet can be crafted
            {
                byte[] pubKey = Account.addressToPublicKey(AccountDataContainer.account);
                returnBytes = new byte[1+pubKey.length];
                returnBytes[0] = IncomingRequestHeaders.ACCOUNT_REQUEST;
                System.arraycopy(pubKey, 0, returnBytes, 1, pubKey.length);
            }
        }
        catch(Exception e)
        {
            Log.w(TAG, "Error", e);
        }

        if(returnBytes==null)
            returnBytes = new byte[] { (byte)0x90, (byte)0x00 };

        //Log.w(TAG, "RETURNING length "+ returnBytes.length);
        return returnBytes;
    }

    void createInvoice()
    {
        signatureForBox = null;
        invoice = new ByteInvoice(invoiceArray);

        MainActivity.currentInvoice = invoice;
        double amount_local_curr_rounded = Math.round(invoice.localCurrAmount*100.0)/100.0;

        /*
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
        System.out.println(df.format(invoice.localCurrAmount));*/

        String localCurrAmountFormatted = String.format("%.2f", amount_local_curr_rounded).replace(',', '.');

        // Attention please: In other to enable the PAY button, the string passed to printMessage currently needs to start with "Request:"
        printMessage("Request: " + invoice.storeName + " - " + invoice.spendAmountNANO+" NANO = "+ localCurrAmountFormatted + " " + invoice.localCurr);

    }

    // END_INCLUDE(processCommandApdu)

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private void printMessage(String text) {
        try
        {
            Message msg = new Message();
            msg.obj = text;
            MainActivity.thisMainActivity.handler.sendMessage(msg);
        } catch (Exception ex)
        {
            int bp = 7;
        }
    }
}

