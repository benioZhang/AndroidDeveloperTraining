package com.benio.training.class12;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.benio.training.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Class12Activity extends AppCompatActivity {

    static final int REQUEST_THUMBNAIL = 1;
    static final int REQUEST_FULL_SIZE = 2;

    private ImageView mImageView;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class12);
        mImageView = (ImageView) findViewById(R.id.image);
    }

    public void getThumbnail(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_THUMBNAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_THUMBNAIL && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_FULL_SIZE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    public void getFullSize(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the file where photo should go.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue only if the file was successfully created.
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        getString(R.string.authority),
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_FULL_SIZE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intent
        // 加了file:前缀后，BitmapFactory.decodeFile的时候会报FileNotFound错
        mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();
        return image;
    }

    /**
     * Note: If you saved your photo to the directory provided by getExternalFilesDir(),
     * the media scanner cannot access the files because they are private to your app.
     */
    private void galleryAddPic() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetH = mImageView.getHeight();
        int targetW = mImageView.getWidth();

        // Get the dimensions of the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        int photoH = options.outHeight;
        int photoW = options.outWidth;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoH / targetH, photoW / targetW);

        // Decode the image file to Bitmap sized to fill the view
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        mImageView.setImageBitmap(bitmap);
    }
}
