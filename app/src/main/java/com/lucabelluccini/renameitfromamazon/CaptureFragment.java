package com.lucabelluccini.renameitfromamazon;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class CaptureFragment extends Fragment {

    CaptureInterface callback;

    public interface CaptureInterface {
        public String getAmazonText();
    }

    public CaptureFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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

        return rootView;
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public void dispatchCaptureIntent() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Prepare file and set it as extra parameter of intent
        try {
            File captureFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), callback.getAmazonText() + ".jpg");
            if(captureFile.exists())
                Toast.makeText(getActivity(), "File already exists, will be overwritten!", Toast.LENGTH_SHORT).show();
            Uri captureFileUri = Uri.fromFile(captureFile);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFileUri);
        } catch(NullPointerException e) {
            Toast.makeText(getActivity(), "Failed to create file!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Finally trigger it
        if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getActivity(), "No Activity to handle Capture", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent captureDataIntent) {
        super.onActivityResult(requestCode, resultCode, captureDataIntent);

        // Handle Capture intent bundle and set it to preview
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Capture completed successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Capture was not successful", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (CaptureInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CaptureInterface");
        }
    }

}
