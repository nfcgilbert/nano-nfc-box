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

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity
{
    public static Handler handler;
    public static ByteInvoice currentInvoice = null;

    static MainActivity thisMainActivity;

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
/*
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        thisMainActivity = this;
        CardService.setMainActivity(this);

        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                String payload = (String) msg.obj;
                printMessage(payload);
            }
        };

        theCustomStuff();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    static Button btn_pay;
    static TextView text_info;

    NfcAdapter nfcAdapter;
    String msg_noNFCSupport = "Your device does not support NFC!";
    String msg_NFCDisabled = "NFC is disabled on your device!";
    void theCustomStuff()
    {
        initialiseGUIElements();
    }
/*
    public static SharedPreferences getSharedPreferencesForeign()
    {
        return thisMainActivity.getSharedPreferences("app_preferences", MODE_PRIVATE);
    }*/
    /*
    public static Intent getIntentForeign()
    {
        return thisMainActivity.getIntent();
    }*/

    public void printMessage(String msg)
    {
        TextView err = (TextView) findViewById(R.id.Label_Infotext);
        err.setText(msg);

        if(msg.startsWith("Request:")) {
            btn_pay.setEnabled(true);
        }
        if(msg.startsWith("Decided:")) {
            btn_pay.setEnabled(false);
        }
    }
/*
    void attemptToShowNFCWarning()
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, msg_noNFCSupport, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, msg_NFCDisabled, Toast.LENGTH_LONG).show();
        }
    }*/

    static {
        System.loadLibrary("ed25519-lib");
    }
    private native byte[] sign( byte[] message, byte[] privateKeyJava, byte[] publicKeyJava);

    public byte[] signPublic( byte[] message, byte[] privateKeyJava, byte[] publicKeyJava)
    {
        return sign( message, privateKeyJava, publicKeyJava);
    }

    String turnNANOAmountIntoRAW(String nanoAmount)
    {
        String tempStr=null;
        if(nanoAmount.contains("."))
        {
            String[] parts = nanoAmount.split("\\.");
            while (parts[1].length() < 30) {
                parts[1] += "0";
            }
            tempStr = parts[0] + parts[1];
            while (tempStr.charAt(0) == '0')
                tempStr = tempStr.substring(1);
        }
        else
        {
            tempStr=nanoAmount;
            for (int i=0; i < 30; i++)
            {
                tempStr += "0";
            }
        }
        return tempStr;
    }

    void initialiseGUIElements()
    {
        btn_pay = (Button) findViewById(R.id.Btn_Pay);

        btn_pay.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.w("CLICK", "CLICKED BUTTON");

                BigInteger rawSpendAmount = new BigInteger( turnNANOAmountIntoRAW(String.valueOf(currentInvoice.spendAmountNANO)));
                BigInteger oldAmount = currentInvoice.rawBalanceBefore;
                BigInteger newRaw = oldAmount.subtract(rawSpendAmount);

                if(newRaw.compareTo(BigInteger.valueOf(0))>=0 || true)
                {
                    TransactionSend txSend = new TransactionSend(currentInvoice.previousBlock, null, currentInvoice.boxAddress, newRaw, currentInvoice.appRepAddress, AccountDataContainer.account);
                    String signatureString = txSend.signature;
                    CardService.signatureForBox = DataManipulationUtil.hexStringToByteArray(txSend.signature);
                    MainActivity.currentInvoice.paymentDecisionStatus = IncomingRequestHeaders.PAYMENT_DECISION_PAY;
                    Log.w("Signature", signatureString);
                    printMessage("Move your phone over the reader to pay");
                }
                else
                {
                    printMessage("You cannot afford this!");
                }
            }
        });

        text_info  = (TextView) findViewById(R.id.Label_Infotext);

        btn_pay.setEnabled(false);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
