package com.example.recipesforsuccess.dataobjects;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.recipesforsuccess.R;

public class NutritionalInfo extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nutritioanl_info_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * .6), (int)(dm.heightPixels * 0.8));
    }
}
