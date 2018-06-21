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

import java.math.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.Application;
import android.util.Log;


public class ByteInvoice
{
    public byte paymentDecisionStatus=IncomingRequestHeaders.PAYMENT_DECISION_UNDECIDED;

    public String storeName=null;
    public String localCurr=null;
    public double localCurrAmount=0;
    public double exchangeRate=0;
    public String boxAddress=null;
    public BigInteger rawBalanceBefore=null;
    public String spendAmountNANO=null;
    public String previousBlock=null;
    public String appRepAddress =null;

    public ByteInvoice(byte[] invoiceData)
    {
        int startRawBalance = ((invoiceData[0] & 0xff) << 8) | (invoiceData[1] & 0xff);
        int startSendAmount = ((invoiceData[2] & 0xff) << 8) | (invoiceData[3] & 0xff);
        int startShopName = ((invoiceData[4] & 0xff) << 8) | (invoiceData[5] & 0xff);
        int startLocalCurr = ((invoiceData[6] & 0xff) << 8) | (invoiceData[7] & 0xff);
        int startExchangeRate = ((invoiceData[8] & 0xff) << 8) | (invoiceData[9] & 0xff);

        /*Log.w("+++startRawBalance", ""+startRawBalance);
        Log.w("+++startSendAmount", ""+startSendAmount);
        Log.w("+++startShopName", ""+startShopName);
        Log.w("+++startLocalCurr", ""+startLocalCurr);
        Log.w("+++startExchangeRate", ""+startExchangeRate);*/

        byte[] pubKeyBoxByteConverted = new byte[32];
        System.arraycopy(invoiceData, 10, pubKeyBoxByteConverted, 0, 32);
        boxAddress = Account.publicKeyToXRBAddress(pubKeyBoxByteConverted);
        //Log.w("++boxAddress++", boxAddress);

        byte[] previousBlockByteConverted = new byte[32];
        System.arraycopy(invoiceData, 42, previousBlockByteConverted, 0, 32);
        previousBlock = DataManipulationUtil.nibble_ByteArrayToString(previousBlockByteConverted);
        //Log.w("++previousBlock++", previousBlock);

        byte[] appRepAddressByteConverted = new byte[32];
        System.arraycopy(invoiceData, 74, appRepAddressByteConverted, 0, 32);
        appRepAddress = Account.publicKeyToXRBAddress(appRepAddressByteConverted);
        Log.w("++appRepAddress++", appRepAddress);

        int rawBalanceLength = startSendAmount - startRawBalance;
        int sendAmountLength = startShopName - startSendAmount;
        int shopNameLength = startLocalCurr - startShopName;
        int localCurrLength = startExchangeRate - startLocalCurr;
        int exchangeRateLength = invoiceData.length - startExchangeRate;

        byte[] storeNameBytes = new byte[shopNameLength];
      //  Log.w("+++store vars = ", "" + startShopName+" .... " +shopNameLength);
        System.arraycopy(invoiceData, startShopName, storeNameBytes, 0, shopNameLength);
        storeName = new String(storeNameBytes);
        //Log.w("++storeName++", storeName);

        byte[] localCurrBytes = new byte[localCurrLength];
       // Log.w("+++local curr vars = ", "" + startLocalCurr+" .... " +localCurrLength);
        System.arraycopy(invoiceData, startLocalCurr, localCurrBytes, 0, localCurrLength);
        localCurr = new String(localCurrBytes);
        //Log.w("++localCurr++", localCurr);

        byte[] initialRawBalanceBytes = new byte[rawBalanceLength];
       // Log.w("+++rawBalance vars = ", "" + rawBalanceLength+" .... " +rawBalanceLength);
        System.arraycopy(invoiceData, startRawBalance, initialRawBalanceBytes, 0, rawBalanceLength);
        try
        {
            String rawBalanceTempString = DataManipulationUtil.nibble_ByteArrayToString(initialRawBalanceBytes);
          //  Log.w("TEMPSTR", rawBalanceTempString);
            while(rawBalanceTempString.startsWith("0"))
                rawBalanceTempString = rawBalanceTempString.substring(1);
            rawBalanceBefore = new BigInteger(rawBalanceTempString);
           // Log.w("++rawBalanceBefore++", "" + rawBalanceBefore);
        } catch(Exception e)
        {
            Log.w("Error", "error",e);
            throw new RuntimeException("This is a crash");
        }

        byte[] sendAmountBytes = new byte[sendAmountLength];
       // Log.w("sendAmountBytes", ""+sendAmountBytes.length);
        System.arraycopy(invoiceData, startSendAmount, sendAmountBytes, 0, sendAmountLength);
        try
        {
            spendAmountNANO = new String(sendAmountBytes, "ASCII");
        }
        catch(Exception e)
        {
            Log.w("Error", "error",e);
            throw new RuntimeException("This is a crash");
        }

       // DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
       // df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
       // Log.w("++spendAmountNANO++", ""+df.format(spendAmountNANO));

        byte[] exchangeRateBytes = new byte[exchangeRateLength];
        System.arraycopy(invoiceData, startExchangeRate, exchangeRateBytes, 0, exchangeRateLength);
        try
        {
            exchangeRate = Double.parseDouble(new String(exchangeRateBytes, "ASCII"));
        }
        catch(Exception e)
        {
            Log.w("Error", "error",e);
            throw new RuntimeException("This is a crash");
        }
        //Log.w("++appRepAddress++", appRepAddress);

        localCurrAmount = Double.parseDouble((new BigDecimal(spendAmountNANO).multiply(new BigDecimal(exchangeRate))).toString());

    }
}
