package com.example.owner.testapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by owner on 2015/06/14.
 */
public class DownloadImageAsyncTaskLoaderHelper extends AsyncTaskLoader<Bitmap> {

    private String imageUrl = "";
    private Context context = null;

    public DownloadImageAsyncTaskLoaderHelper(Context context, String url) {
        super(context);

        this.imageUrl = url;
        this.context = context;
    }

    @Override
    public Bitmap loadInBackground() {
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}