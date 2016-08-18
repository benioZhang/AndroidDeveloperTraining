package com.benio.training.class14;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<AsyncTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, AsyncTask task) {
        super(res, bitmap);
        bitmapWorkerTaskReference = new WeakReference<AsyncTask>(task);
    }

    public AsyncTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}