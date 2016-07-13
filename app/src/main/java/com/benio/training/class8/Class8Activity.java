package com.benio.training.class8;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.benio.training.R;

import java.util.ArrayList;

public class Class8Activity extends AppCompatActivity {
    private static final int REQUEST_GALLERY_BINARY = 0x0101;
    private static final int REQUEST_GALLERY_MULTIPLE = 0x0102;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class8, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "This is my word to send");
        intent.setType("text/plain");

        shareActionProvider.setShareIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class8);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            } else if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    public void shareText(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "This is my word to send");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share to.."));
    }

    public void shareBinary(View view) {
        openGallery(REQUEST_GALLERY_BINARY);
    }

    public void shareMultiplePieces(View view) {
        openGallery(REQUEST_GALLERY_MULTIPLE);
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_BINARY && data != null) {
            Uri uriToImage = data.getData();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            startActivity(Intent.createChooser(intent, "Share to.."));
        } else if (requestCode == REQUEST_GALLERY_MULTIPLE && data != null) {
            Uri imageUri1 = data.getData();
            Uri imageUri2 = data.getData();
            ArrayList<Uri> imageUris = new ArrayList<>();
            imageUris.add(imageUri1);
            imageUris.add(imageUri2);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share to.."));
        }
    }
}
