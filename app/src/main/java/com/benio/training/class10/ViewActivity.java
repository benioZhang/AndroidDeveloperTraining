package com.benio.training.class10;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.benio.training.R;

import java.io.File;

public class ViewActivity extends AppCompatActivity {

    // A File object containing the path to the transferred files
    private File mParentPath;
    // Incoming Intent
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        handleViewIntent();
    }

    private void handleViewIntent() {
        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value.
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(beamUri.getScheme(), "content")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
    }

    private File handleFileUri(Uri beamUri) {
        // Get the path of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        return copiedFile.getParentFile();
    }

    private File handleContentUri(Uri beamUri) {
        // Test the authority of URI
        if (TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             *Handle content URIs for other content providers.
             */
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name.
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor pathCursor = getContentResolver().query(beamUri, projection, null, null, null);
            // Check for valid cursor.
            if (pathCursor != null && pathCursor.moveToFirst()) {
                String fileName = pathCursor.getString(pathCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                File copiedFile = new File(fileName);
                return copiedFile.getParentFile();
            }
            // The query didn't work; return null
            return null;
        }
        return null;
    }
}
