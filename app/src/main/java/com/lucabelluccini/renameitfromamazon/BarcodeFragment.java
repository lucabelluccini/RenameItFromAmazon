package com.lucabelluccini.renameitfromamazon;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeFragment extends Fragment {

    public String getBarcodeText() {
        return barcodeText.getText().toString();
    }

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

        // Set barcode to UI
        barcodeText = (EditText) rootView.findViewById(R.id.barcode_edittext);

        return rootView;
    }

    public void dispatchBarcodeIntent() {
        IntentIntegrator barcodeIntent = new IntentIntegrator(this);
        barcodeIntent.addExtra("SCAN_MODE", "PRODUCT_MODE");
        barcodeIntent.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        AlertDialog downloadScannerAD = barcodeIntent.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
        if(downloadScannerAD != null)
        {
            Toast.makeText(getActivity(), "Cannot perform Barcode", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent scanDataIntent) {
        super.onActivityResult(requestCode, resultCode, scanDataIntent);

        // Handle barcode scan result and set it to editText
        IntentResult barcodeIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, scanDataIntent);
        if (barcodeIntentResult != null) {
            String barcodeContent = barcodeIntentResult.getContents();
            if (barcodeContent != null) {
                // Set Barcode to UI
                barcodeText.setText(barcodeContent);
            } else {
                Toast.makeText(getActivity(), "Barcode was invalid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Barcode was unsuccessful", Toast.LENGTH_SHORT).show();
        }

    }



}