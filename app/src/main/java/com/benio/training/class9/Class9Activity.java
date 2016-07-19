package com.benio.training.class9;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.benio.training.R;

import java.io.FileNotFoundException;

public class Class9Activity extends AppCompatActivity {
    private static final String TAG = "Class9Activity";
    public static final int REQUEST_SELECT_FILE = 0x00002;

    private Intent mRequestFileIntent;
    private ParcelFileDescriptor mInputPFD;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class9);

        mTextView = (TextView) findViewById(R.id.fileinfo);
        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("text/plain");
    }

    public void select(View view) {
        startActivityForResult(mRequestFileIntent, REQUEST_SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK && data != null) {
            // Get the file's content URI from the incoming Intent
            Uri returnUri = data.getData();
            String mimeType = getContentResolver().getType(returnUri);
            /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */
            try {
                mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "File not found.");
                return;
            }
            // Get a regular file descriptor for the file
            //FileDescriptor fd = mInputPFD.getFileDescriptor();
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            String size = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
            cursor.close();
            mTextView.setText("MIME:" + mimeType + "\nDISPLAY_NAME:" + name + "\nSIZE:" + size);
        }
    }
}
