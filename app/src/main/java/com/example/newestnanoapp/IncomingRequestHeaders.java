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

public class IncomingRequestHeaders
{
    public static byte INCOMING_INVOICE_START           = 0x01;
    public static byte INCOMING_INVOICE_FOLLOWUP        = 0x02;
    public static byte SIGNED_PACKET_REQUEST            = 0x03;
    public static byte ACCOUNT_REQUEST                  = 0x04;
    public static byte PAYMENT_DECISION_PAY             = 0x05;
    public static byte FINAL_RESULT_HEADER              = 0x06;
    public static byte SIGNED_PACKET_RECEIVED_OK        = 0x13;

    public static byte SEND_AGAIN                       = 0x11;
    public static byte PAYMENT_DECISION_UNDECIDED       = 0x12;
}
