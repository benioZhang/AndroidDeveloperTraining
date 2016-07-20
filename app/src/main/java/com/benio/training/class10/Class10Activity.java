package com.benio.training.class10;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.benio.training.R;

import java.io.File;

public class Class10Activity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    // Flag to indicate that Android Beam is available
    boolean mAndroidBeamAvailable = false;
    // List of URIs to provide to Android Beam
    private Uri[] mFileUris = new Uri[10];
    // Instance that returns available files from this app
    private FileUriCallback mFileUriCallback;

    /**
     * Callback that Android Beam file transfer calls to get
     * files to share
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
        }

        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class10);
        // NFC isn't available on the device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            Toast.makeText(Class10Activity.this, "NFC isn't available", Toast.LENGTH_SHORT).show();
            /*
             * Disable NFC features here.
             * For example, disable menu items or buttons that activate
             * NFC-related features
             */
            // Android Beam file transfer isn't supported
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Toast.makeText(Class10Activity.this, "Android Beam file transfer isn't supported", Toast.LENGTH_SHORT).show();
            // If Android Beam isn't available, don't continue.
            mAndroidBeamAvailable = false;
            /*
             * Disable Android Beam file transfer features here.
             */
            // Android Beam file transfer is available, continue
        } else {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            /*
             * Instantiate a new FileUriCallback to handle requests for
             * URIs
             */
            mFileUriCallback = new FileUriCallback();
            // Set the dynamic callback for URI requests.
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        }

    }

    private void fileToTransfer() {
        /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
        String transferFile = "transferimage.jpg";
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        Uri fileUri = Uri.fromFile(requestFile);
        if (fileUri != null) {
            mFileUris[0] = fileUri;
        } else {
            Log.e("My Activity", "No File URI available for file.");
        }
    }
}
