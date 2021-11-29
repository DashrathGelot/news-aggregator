package com.example.newsaggregator.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.newsaggregator.MainActivity;

import java.io.InputStream;

public class ImageService extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    MainActivity mainActivity;

    public ImageService(ImageView imageView, MainActivity mainActivity) {
        this.imageView = imageView;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap mIcon11;
        try {
            InputStream in = new java.net.URL(strings[0]).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            mIcon11 = null;
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            imageView.setImageResource(mainActivity.getResources().getIdentifier("brokenimage", "drawable", mainActivity.getPackageName()));
        } else {
            imageView.setImageBitmap(result);
        }
    }
}
