package com.example.recipesforsuccess;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

public class MainPage extends MainActivity {
    private static final String TAG = "NAVBAR";
    private RadioGroup navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        // How NavBar was made
        // https://stackoverflow.com/questions/41740632/how-to-change-activity-on-bottom-navigation-button-click
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
                    default:
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

}
