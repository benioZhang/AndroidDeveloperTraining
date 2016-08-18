package com.benio.training.class14;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.benio.training.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ManagingBitmapActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Bitmap mPlaceHolderBitmap;
    private ImageAdapter mAdapter;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    private LruCache<String, BitmapDrawable> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    public static final String DISK_CACHE_SUBDIR = "thumbnails";
    private Set<SoftReference<Bitmap>> mReusableBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_bitmap);

        // Use max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        if (Utils.hasHoneycomb()) {
            mReusableBitmaps =
                    Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        mMemoryCache = retainFragment.retainCache;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(String key, BitmapDrawable value) {
                    final int bitmapSize = getBitmapSize(value) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                    if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
                        // The removed entry is a recycling drawable, so notify it
                        // that it has been removed from the memory cache.
                        ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
                    } else {
                        // The remove entry is a standard BitmapDrawable
                        if (Utils.hasHoneycomb()) {
                            mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                        }
                    }
                }
            };
            retainFragment.retainCache = mMemoryCache;
        }

        // Initialize disk cache on background thread.
        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(this);
        final GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(mAdapter);
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int numColumns = (int) Math.floor(gridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                if (numColumns > 0) {
                    final int columnWidth = gridView.getWidth() / numColumns - mImageThumbSpacing;
                    mAdapter.setItemHeight(columnWidth);
                    if (Utils.hasJellyBean()) {
                        gridView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    } else {
                        gridView.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        gridView.setOnItemClickListener(this);
    }

    // Create a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (Utils.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(this, ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private final int[] imageArray = Images.IMAGES;
        private final Context mContext;
        private int mItemHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return imageArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else {
                imageView = (ImageView) convertView;
            }

            // check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            loadBitmap(imageArray[position % imageArray.length], imageView);
            return imageView;
        }

        public void loadBitmap(int resId, ImageView imageView) {
            BitmapDrawable value = getBitmapFroMemoryCache(String.valueOf(resId));
            if (value != null) {
                // Bitmap found in memory cache
                imageView.setImageDrawable(value);
            } else if (cancelPotentialWork(resId, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView, mItemHeight);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }
    }

    private BitmapDrawable getBitmapFroMemoryCache(String key) {
        BitmapDrawable memValue = null;
        if (mMemoryCache != null) {
            memValue = mMemoryCache.get(key);
        }
        return memValue;
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread.
            while (mDiskCacheStarting) {
                try {
                    mDiskLruCache.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDiskLruCache != null) {
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        InputStream inputStream = snapshot.getInputStream(0);
                        return BitmapFactory.decodeStream(inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void addBitmapToCache(String key, BitmapDrawable drawable) {
        addBitmapToMemoryCache(key, drawable);

        // Also add to disk cache
        addBitmapToDiskCache(key, drawable);
    }

    private void addBitmapToDiskCache(String data, BitmapDrawable drawable) {
        synchronized (mDiskCacheLock) {
            final String key = data;
            OutputStream out = null;
            try {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        out = editor.newOutputStream(0);
                        drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
                        editor.commit();
                        out.close();
                    }
                } else {
                    snapshot.getInputStream(0).close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
        // Add to memory cache
        if (mMemoryCache != null) {
            if (RecyclingBitmapDrawable.class.isInstance(drawable)) {
                // The removed entry is a recycling drawable, so notify it
                // that it has been added into the memory cache
                ((RecyclingBitmapDrawable) drawable).setIsCached(true);
            }
            mMemoryCache.put(key, drawable);
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

    private static final String TAG = "ManagingBitmapActivity";

    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;
                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    static boolean canUseForInBitmap(Bitmap bitmap, BitmapFactory.Options options) {
        if (Utils.hasKitKat()) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = options.outWidth / options.inSampleSize;
            int height = options.outHeight / options.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(bitmap.getConfig());
            return byteCount <= bitmap.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return bitmap.getWidth() == options.outWidth
                && bitmap.getHeight() == options.outHeight
                && options.inSampleSize == 1;
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, BitmapDrawable> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private int mImageSize;

        public BitmapWorkerTask(ImageView imageView, int imageSize) {
            // Use a WeakReference to ensure the ImageView can be garbage collected.
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.mImageSize = imageSize;
        }

        // Decode image in background.
        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            data = params[0];
            String key = String.valueOf(data);

            Resources resources = getResources();
            Bitmap bitmap = null;
            // Check disk cache in background thread
            if (!isCancelled() && getAttachedImageView() != null) {
                // Process as normal
                bitmap = getBitmapFromDiskCache(key);
            }

            if (bitmap == null && !isCancelled() && getAttachedImageView() != null) {
                bitmap = BitmapUtil.decodeSampleBitmapFromResource(resources,
                        data, mImageSize, mImageSize);

                // First decode with inJustDecodeBounds = true to check dimensions.
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                if (Utils.hasHoneycomb()) {
                    // inBitmap only works with mutable bitmaps so force the decoder to
                    // return mutable bitmaps.
                    options.inMutable = true;

                    // Try and find a bitmap to use for inBitmap
                    Bitmap inBitmap = getBitmapFromReusableSet(options);
                    if (inBitmap != null) {
                        options.inBitmap = inBitmap;
                    }
                }
                // Calculate inSampleSize
                options.inSampleSize = BitmapUtil.calculateInSampleSize(options, mImageSize, mImageSize);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;

                BitmapFactory.decodeResource(resources, data, options);
            }

            BitmapDrawable drawable = null;
            if (bitmap != null) {
                if (Utils.hasHoneycomb()) {
                    // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
                    drawable = new BitmapDrawable(resources, bitmap);
                } else {
                    // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
                    // which will recycle automatically
                    drawable = new RecyclingBitmapDrawable(resources, bitmap);
                }
                addBitmapToCache(key, drawable);
            }
            // add final bitmap to cache
            return drawable;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            if (isCancelled()) {
                drawable = null;
            }

            if (drawable != null) {
                final ImageView imageView = getAttachedImageView();
                if (imageView != null) {
                    imageView.setImageDrawable(drawable);
                }
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragment";
        public LruCache<String, BitmapDrawable> retainCache;

        public RetainFragment() {
        }

        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    mDiskCacheStarting = false; // Finish initialization.
                    mDiskCacheLock.notifyAll(); // Wake any waiting thread.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
