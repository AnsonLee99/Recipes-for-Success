package com.example.recipesforsuccess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewRecipeInstructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe_instructions);

        // Hide action bar
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        TextView instructions = (TextView) findViewById(R.id.instruction_text);
        TextView recipeText = (TextView) findViewById(R.id.recipe_name);
        ImageView imgView = (ImageView) findViewById(R.id.recipe_img);

        Intent intent = getIntent();
        String recipeName = intent.getStringExtra("recipeName");
        System.out.println("recipe name is: " + recipeName);
        instructions.setText(intent.getStringExtra("instructions"));
        instructions.setMovementMethod(new ScrollingMovementMethod());
        recipeText.setText(recipeName);
        Picasso.with(getApplicationContext()).load(intent.getStringExtra("imgURL")).into(imgView);

    }
}
