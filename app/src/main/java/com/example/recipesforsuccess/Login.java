package com.example.recipesforsuccess;

import android.app.Application;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.*;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {


    ConstraintLayout mainDisplay;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        /*
        mainDisplay = (ConstraintLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_login, null);
        mainDisplay.addView(groceryView);
        */
        /*
        if(!FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        */
    }
}
