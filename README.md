# nano-nfc-box
Demo code for the protocol of the Nano Blackbox

## The Nano Blackbox...
... is an NFC cashpoint that utilises the cryptocurrency Nano. It is currently in the last phase of its development. It's not yet fully ready.

## This repo...
... contains an example Android app to communicate with the Nano Blackbox. I guess without the box itself it will be impossible to test for you at the moment.

## What's required to run this?
A smartphone that can do NFC and runs an Android version that supports it.  

## More information?
You may find more information on https://nanoblackbox.net

# The protocol

## The Nano Blackbox NFC protocol (will be updated to be more readable soon)

## Basics: 

1. The smartphone must emulate a smartcard.

2. Before every sending action, the box sends the following: { 0x00, 0xA4, 0x04, 0x00, 0x05, 0xF2, 0xA7, 0x31, 0xD8, 0x7C}.
0x00 0xA4 0x04 0x00 is the "select file" command, 0x05 the number of following bytes, and 0xA7, 0x31 etc the app id. It selects the app to communicate with it.
See also: ISO 7816 Part 4

3. The phone can only reply to nfc messages that are sent to it.

4. The maximum size of an nfc message is 255 bytes.

5. The box resends requests/messages if a return message does not have the expected format.

## The protocol:

### Step 1: The box first requests the xrb_ account of the app/customer by sending the byte 0x04.
> App reply: The app replies with an array that starts with 0x04 as well, then a byte array of the public key of the customer's account, 32 bytes long.

### Step 2: The box sends the invoice. It contains everything the app needs in order to display the invoice properly and create the signature to authorise the payment.  

The invoice byte array is structured like this:  
bytes[0]+[1]: start of the raw balance of the customer's account before the transaction (in RAW) inside the byte array  
bytes[2]+[3]: start of the requested amount (in Nano such as shown in the block explorer) inside the byte array  
bytes[4]+[5]: start of the shop name inside the byte array  
bytes[6]+[7]: start of the local currency (EUR, USD, CAD, etc.) inside the byte array  
bytes[8]+[9]: start of the exchange rate inside the byte array (1 Nano to x local currency)  
  
This is followed by:  
32 bytes for the public key of the box/invoice address.  
32 bytes for the previous block hash of the customer's account  
32 bytes for the representative of the app/customer's account  
  
Followed by:  
Bytes of variable length for the raw balance, the requested amount, the shop name, the local currency, and the exchange rate. Their start indexes are defined in the beginning of the array as described above.  
  
The whole invoice packet looks like this:  
  
-- Case 1 (invoice <= 252 bytes)  
  
byte[0] = 0x01  
byte[1]+[2] = the length of the following bytes  
byte[3] and on = the invoice  
> App reply: The length of the message bytes (up to 252)  
  
-- Case 2 (invoice > 252 bytes)  
  
The same as above, but there is a second packet:  
  
byte[0] = 0x02  
byte[1] and on: The rest of the invoice bytes  
> App reply: The length of the followup message bytes (up to 254)  
  
### Step 3: The box "polls" the app for the signature  
  
The box keeps sending the byte 0x03.  
> App reply: If the customer chooses to pay, the app replies with the signature for this packet.  
  
### Step 4: The result:  
  
The result will not always come back immediately. Proof of Work might take a bit longer and the customer could have already moved his phone away from the nfc reader by then. If the box gets to send a final result, it looks like this:  
  
byte[0] = 0x06  
byte[1] = Length of the message // Remark: This byte might appear pointless at first. But it serves to check if the message has been fully received so the app can instead display that it received a garbled result instead of displaying something wrong such as a new wrong balance.  
byte[2] and on: The final result  

The result is a string message. It starts with "e:" for error (followed by the error) or "b:" (followed by the new balance) for the new balance in RAW.  
  
> No app reply to this.
