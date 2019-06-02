package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ViewRecipeInstructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe_instructions);

        // Hide action bar
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        //TextView instructions = (TextView) findViewById(R.id.instruction_text);
        //TextView recipeText = (TextView) findViewById(R.id.recipe_name);
        //ImageView imgView = (ImageView) findViewById(R.id.recipe_img);

        LinearLayout layout = (LinearLayout)findViewById(R.id.recipe_instruct_page);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        //findViewById(R.id.recipe_instruct_screen).setBackgroundColor(889176327);

        // Star for title
        ImageButton favButton = (ImageButton) findViewById(R.id.fav_button);
        favButton.setImageResource(android.R.drawable.btn_star);
        favButton.setBackgroundColor(Color.WHITE);
        favButton.setPadding(0,50,0,0);

        // Recipe Name
        TextView recipeName = findViewById(R.id.recipe_title);
        recipeName.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 500));
        recipeName.setText(getIntent().getStringExtra("recipeName"));
        recipeName.setTextSize(28);
        recipeName.setTextColor(Color.BLACK);
        recipeName.setGravity(Gravity.CENTER_VERTICAL);
        recipeName.setBackgroundColor(Color.WHITE);
        recipeName.setPadding(50,-50,0,-10);

        // Recipe Image
        ImageView imgView = (ImageView)findViewById(R.id.recipe_image);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1100));
        imgView.setPadding(0,0,0,0);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Line after recipe name
        TextView indentSpace = findViewById(R.id.indent_space);
        indentSpace.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 50));
        indentSpace.setBackground(getDrawable(R.drawable.recipe_instruction_border_1));

        // instructions
        Intent intent = getIntent();
        String instructionString = intent.getStringExtra("instructions");
        TextView instructions = findViewById(R.id.instructions_text);
        instructions.setBackgroundColor(Color.WHITE);
        instructions.setPadding(40, 75, 40, 50);
        instructions.setTextColor(Color.BLACK);
        instructions.setText(instructionString);

        // adding everything to the layout
        Picasso.with(getApplicationContext()).load(intent.getStringExtra("imgURL")).into(imgView);

    }
}
