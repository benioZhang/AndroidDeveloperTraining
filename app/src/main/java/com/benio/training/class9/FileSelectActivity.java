package com.benio.training.class9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.benio.training.R;

import java.io.File;
import java.io.IOException;

public class FileSelectActivity extends AppCompatActivity {
    // The path to the root of this app's internal storage
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    String[] mImageFilenames;

    private Intent mResultIntent;

    private ListView mFileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        createTxtFile(String.valueOf(System.currentTimeMillis()));

        // Set up an Intent to send back to apps that request a file
        mResultIntent = new Intent("com.example.myapp.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "images");
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();

        mImageFilenames = mImagesDir.list();
        if (mImageFilenames == null) {
            mImageFilenames = new String[]{};
        }
        // Set the Activity's result to null to begin with
        setResult(RESULT_OK, null);
        /*
         * Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         */
        mFileListView = (ListView) findViewById(R.id.list);
        ListAdapter adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mImageFilenames);
        mFileListView.setAdapter(adapter);
        // Define a listener that responds to clicks on a file in the ListView
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * mImageFilename array.
                 */
                File requestFile = mImageFiles[position];
                Uri fileUri = null;
                try {
                    fileUri = FileProvider.getUriForFile(
                            FileSelectActivity.this,
                            getString(R.string.authority),
                            requestFile
                    );
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.e("File Selector",
                            "The selected file can't be shared: " +
                                    requestFile.getAbsolutePath());
                }

                if (fileUri != null) {
                    // Grant temporary read permission to the content URI
                    mResultIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    // Put the Uri and MIME type in the result Intent
                    mResultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                    // Set the result
                    setResult(RESULT_OK, mResultIntent);
                } else {
                    mResultIntent.setDataAndType(null, "");
                    setResult(RESULT_CANCELED, mResultIntent);
                }

                finish();
            }
        });

    }

    private File createTxtFile(String name) {
        File dir = new File(getFilesDir(), "images");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            return File.createTempFile(name, ".txt", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
