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

public abstract class Transaction {
    private final Type type;

    public Transaction(Type type/*, String work*/) {
        this.type = type;
     //   this.work = work;
    }

    public final Type getType() {
        return this.type;
    }

    abstract String calculateSignature();

    public abstract String getAsJSON();

    public enum Type {
        OPEN, SEND, RECEIVE, CHANGE
    }
}
