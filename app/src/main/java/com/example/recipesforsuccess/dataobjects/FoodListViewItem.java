package com.example.recipesforsuccess.dataobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.recipesforsuccess.R;

import java.io.InputStream;

public class FoodListViewItem {

    String name;
    String dateAdded;
    Bitmap img;
    FoodListViewAdapter adapter;

    public FoodListViewItem(String name, String dateAdded, String imgURL, Context current, FoodListViewAdapter adapter) {
        this.name=name;
        this.dateAdded=dateAdded;
        this.img = BitmapFactory.decodeResource(current.getResources(), R.drawable.missing);
        new DownloadImageTask(this.img).execute("https://spoonacular.com/cdn/ingredients_250x250/"+imgURL);
        this.adapter = adapter;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return dateAdded;
    }

    public Bitmap getImage() {
        return img;
    }

    public void updateUI(Bitmap map) {
        this.img = map;
        this.adapter.refreshView();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap img;


        public DownloadImageTask(Bitmap map) {
            this.img = map;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            updateUI(result);
        }
    }
}