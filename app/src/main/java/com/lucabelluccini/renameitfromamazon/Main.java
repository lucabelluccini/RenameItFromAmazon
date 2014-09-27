package com.lucabelluccini.renameitfromamazon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends Activity implements AmazonFragment.AmazonInterface, CaptureFragment.CaptureInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new BarcodeFragment(), "BarcodeFragment")
                    .add(R.id.container, new AmazonFragment(), "AmazonFragment")
                    .add(R.id.container, new CaptureFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public String getQueryText() {
        BarcodeFragment barcodeFragment = (BarcodeFragment) getFragmentManager().findFragmentByTag("BarcodeFragment");
        return barcodeFragment.getBarcodeText();
    }

    @Override
    public String getAmazonText() {
        AmazonFragment amazonFragment = (AmazonFragment) getFragmentManager().findFragmentByTag("AmazonFragment");
        String cleanedAmazonText = amazonFragment.getAmazonText().replaceAll("[\n\r|?*<:>+\\[\\]/'\\\"]", "");
        amazonFragment.setAmazonText(cleanedAmazonText);
        return cleanedAmazonText;
    }
}
