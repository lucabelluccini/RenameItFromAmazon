package com.lucabelluccini.renameitfromamazon;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AmazonFragment extends Fragment {

    AmazonInterface callback;

    public void setAmazonText(String amazonText) {
        this.amazonEditText.setText(amazonText);
    }

    public interface AmazonInterface {
        public String getQueryText();
    }

    private EditText amazonEditText;

    private AsyncHttpClient httpClient;

    public AmazonFragment() {
        httpClient = new AsyncHttpClient();
    }

    public String getAmazonText() {
        return amazonEditText.getText().toString();
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
                dispatchAmazonCall();
            }
        });

        // Capture Preview
        amazonEditText = (EditText) rootView.findViewById(R.id.amazon_edittext);

        return rootView;
    }

    public void dispatchAmazonCall() {
        // Prepare url for Amazon
        String url = "http://www.amazon.it/s/ref=nb_sb_noss?&url=search-alias%3Daps&field-keywords=" + callback.getQueryText();
        // Shoot request
        httpClient.get(url,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Document resultPage = Jsoup.parse(new String(responseBody));
                Elements matchedItems = resultPage.select("div#atfResults div#result_0 .newaps");
                if(!matchedItems.isEmpty()) {
                    // Set the text
                    String result = matchedItems.first().text().trim();
                    amazonEditText.setText(result);
                } else {
                    Toast.makeText(getActivity(), "Couldn't find any match!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Couldn't contact Amazon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (AmazonInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AmazonInterface");
        }
    }

}








