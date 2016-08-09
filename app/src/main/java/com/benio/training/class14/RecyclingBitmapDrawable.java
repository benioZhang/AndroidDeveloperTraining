package com.benio.training.class14;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

/**
 * A BitmapDrawable that keeps track of whether it is being displayed of cached.
 * When the drawable is no longer being displayed of cached,
 * {@link android.graphics.Bitmap#recycle() recycle()} will be called on this drawable's bitmap.
 */
public class RecyclingBitmapDrawable extends BitmapDrawable {
    private static final String TAG = "RecyclingBitmapDrawable";

    private int mCacheRefCount = 0;
    private int mDisplayRefCount = 0;

    private boolean mHasBeenDisplayed;

    public RecyclingBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public RecyclingBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public RecyclingBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
    }

    /**
     * Notify the drawable that the display state has changed.
     * Keep a count to determine when a drawable is no longer displayed.
     *
     * @param isDisplayed Whether the drawable is being displayed or not
     */
    public void setIsDisplayed(boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                mDisplayRefCount++;
                mHasBeenDisplayed = true;
            }
        }
        // Check to see if recycle() can be called
        checkState();
    }

    /**
     * Notify the drawable that the cache state has changed.
     * Keep a count to determine when a drawable is no longer being cached.
     *
     * @param isCached Whether the drawable is being cached or not
     */
    public void setIsCached(boolean isCached) {
        synchronized (this) {
            if (isCached) {
                mCacheRefCount++;
            } else {
                mCacheRefCount--;
            }
        }
        // Check to see if recycle() can be called
        checkState();
    }

    private synchronized void checkState() {
        // If the drawable cache and display reference counts = 0, and this drawable
        // has been displayed, then recycle
        if (mDisplayRefCount <= 0 && mCacheRefCount <= 0 && mHasBeenDisplayed) {
            getBitmap().recycle();
        }
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }

}
