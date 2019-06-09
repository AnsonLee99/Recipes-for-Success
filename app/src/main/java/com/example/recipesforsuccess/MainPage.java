package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainPage extends MainActivity {
    private static final String TAG = "NAVBAR";
    private RadioGroup navBar;
    RadioButton[] tabs = new RadioButton[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        tabs = new RadioButton[4];
        tabs[0] = (RadioButton) findViewById(R.id.grocery_tab_button);
        tabs[1] = (RadioButton) findViewById(R.id.recipes_tab_button);
        tabs[2] = (RadioButton) findViewById(R.id.basket_tab_button);
        tabs[3] = (RadioButton) findViewById(R.id.profile_tab_button);

        navBar = (RadioGroup) findViewById(R.id.NavBar_Group);
        navBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent in;

                switch(checkedId) {
                    case R.id.grocery_tab_button:
                        in = new Intent(getBaseContext(), GroceryList.class);
                        startActivity(in);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.recipes_tab_button:
                        in = new Intent(getBaseContext(), Recipes.class);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.basket_tab_button:
                        in = new Intent(getBaseContext(), Basket.class);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.profile_tab_button:
                        in = new Intent(getBaseContext(), Profile.class);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        break;
                }
            }
        });
    }

    protected void setSelected(int i) {
        for ( int t = 0; t < tabs.length; t ++ ) {
            if ( t == i ) {
                tabs[t].setTypeface(null, Typeface.BOLD);
                tabs[t].setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
            }
            else {
                tabs[t].setTypeface(null, Typeface.NORMAL);
                tabs[t].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            }

        }
    }

}
