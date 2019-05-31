package com.example.recipesforsuccess.dataobjects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.recipesforsuccess.R;

import java.util.concurrent.Callable;

public class FoodListViewItem extends Activity {

    String name;
    String dateAdded;
    int imageid;
    Callable<Void> showPopup;

    public FoodListViewItem(String name, String dateAdded, int imageid, Callable<Void> showPopup) {
        this.name=name;
        this.dateAdded=dateAdded;
        this.imageid=imageid;
        this.showPopup = showPopup;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return dateAdded;
    }

    public int getImageId() { return imageid; }

    public void popUp(Context context) {
        Log.d("test", "POPUPPPP");

        try {
            this.showPopup.call();
        } catch(Exception e) {
            Log.d("test", "Error with  pupup: " + e);
        }
    }


}
