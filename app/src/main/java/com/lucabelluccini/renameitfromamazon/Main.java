package com.lucabelluccini.renameitfromamazon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CaptureFragment())
                    .add(R.id.container, new BarcodeFragment())
                    .add(R.id.container, new AmazonFragment())
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

    /**
     *
     */
    public static class CaptureFragment extends Fragment {

        ImageView capturePreview;

        public CaptureFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_capture, container, false);

            // Capture Button
            Button captureButton = (Button) rootView.findViewById(R.id.capture_button);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchCaptureIntent();
                }
            });

            // Capture Preview
            capturePreview = (ImageView) rootView.findViewById(R.id.capture_image_preview);

            return rootView;
        }

        private static final int REQUEST_IMAGE_CAPTURE = 1;

        public void dispatchCaptureIntent() {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent captureDataIntent) {
            super.onActivityResult(requestCode, resultCode, captureDataIntent);

            // Handle Capture intent bundle and set it to preview
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = captureDataIntent.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Set capture preview
                capturePreview.setImageBitmap(imageBitmap);
            }
        }

    }

    public static class BarcodeFragment extends Fragment {

        EditText barcodeText;

        public BarcodeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_barcode, container, false);

            // Scan Barcode Button
            Button barcodeButton = (Button) rootView.findViewById(R.id.barcode_button);
            barcodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchBarcodeIntent();
                }
            });

            // Capture Preview
            barcodeText = (EditText) rootView.findViewById(R.id.barcode_edittext);

            return rootView;
        }

        public void dispatchBarcodeIntent() {
            IntentIntegrator barcodeIntent = new IntentIntegrator(this);
            barcodeIntent.addExtra("SCAN_MODE", "PRODUCT_MODE");
            barcodeIntent.addExtra("PROMPT_MESSAGE", "Scanner Start!");
            AlertDialog downloadScannerAD = barcodeIntent.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent scanDataIntent) {
            super.onActivityResult(requestCode, resultCode, scanDataIntent);

            // Handle barcode scan result and set it to editText
            IntentResult barcodeIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, scanDataIntent);
            if (barcodeIntentResult != null) {
                String barcodeContent = barcodeIntentResult.getContents();
                if (barcodeContent != null) {
                    barcodeText.setText(barcodeContent.toString());
                }
            }

        }


    }

    public static class AmazonFragment extends Fragment {

        EditText amazonEditText;
        AsyncHttpClient httpClient;

        public AmazonFragment() {
            httpClient = new AsyncHttpClient();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_amazon, container, false);

            // Amazon Button
            Button captureButton = (Button) rootView.findViewById(R.id.amazon_button);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String barcodeValue = ((TextView) get.findViewById(R.id.barcode_textview)).getText().toString();
                    String barcodeValue = "9788873035060";
                    dispatchAmazonCall(barcodeValue);
                }
            });

            // Capture Preview
            amazonEditText = (EditText) rootView.findViewById(R.id.amazon_edittext);

            return rootView;
        }

        public void dispatchAmazonCall(String barcodeValue) {
            // Prepare url for Amazon
            String url = "http://www.amazon.it/s/ref=nb_sb_noss?&url=search-alias%3Daps&field-keywords=" + barcodeValue;
            // Shoot request
            httpClient.get(url,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Document resultPage = Jsoup.parse(new String(responseBody));
                    Elements matchedItems = resultPage.select("div#atfResults div#result_0 .newaps");
                    if(!matchedItems.isEmpty()) {
                        amazonEditText.setText(matchedItems.first().text().toString().trim());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }

    }
}
