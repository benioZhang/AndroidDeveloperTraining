package com.benio.training.class14;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.benio.training.R;

import java.lang.ref.WeakReference;

public class Class14Activity extends AppCompatActivity {

    private Bitmap mPlaceHolderBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class14);

        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MyListAdapter());
    }

    class MyListAdapter extends BaseAdapter {
        private final int[] imageArray = {R.mipmap.image1, R.mipmap.image2, R.mipmap.image3, R.mipmap.image4};
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(parent.getContext());
            }

            loadBitmap(imageArray[position % imageArray.length], (ImageView) convertView);
            return convertView;
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            Resources resources = getResources();
            final BitmapWorkerTask task = new BitmapWorkerTask(resources, imageView);
            AsyncDrawable drawable = new AsyncDrawable(resources, mPlaceHolderBitmap, task);
            imageView.setImageDrawable(drawable);
            task.execute(resId);
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
        if (task != null) {
            final int bitmapData = task.data;
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous work
                task.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existed task was cancelled.
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return (BitmapWorkerTask) ((AsyncDrawable) drawable).getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private Resources resource;
        private int data = 0;

        public BitmapWorkerTask(Resources resource, ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected.
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.resource = resource;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return BitmapUtil.decodeSampleBitmapFromResource(resource, data, 100, 100);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference.get() != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
                if (this == task && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<AsyncTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, AsyncTask task) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<AsyncTask>(task);
        }

        public AsyncTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Memory Cache");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MemoryCacheActivity.class);
        startActivity(intent);
        return true;
    }
}
