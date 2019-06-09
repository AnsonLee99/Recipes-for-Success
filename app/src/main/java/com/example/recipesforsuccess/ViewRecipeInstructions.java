package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        // Recipe Name
        TextView recipeName = findViewById(R.id.recipe_title);
        recipeName.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        recipeName.setText(getIntent().getStringExtra("recipeName"));
        recipeName.setTextSize(28);
        recipeName.setTextColor(Color.BLACK);
        recipeName.setGravity(Gravity.CENTER_VERTICAL);
        recipeName.setBackgroundColor(Color.WHITE);
        recipeName.setPadding(50,30,0,30);

        TextView prepTime = findViewById(R.id.prepTime);
        prepTime.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // Recipe Image
        ImageView imgView = (ImageView)findViewById(R.id.recipe_image);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1100));
        imgView.setPadding(0,0,0,0);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Line after recipe name
        TextView indentSpace = findViewById(R.id.indent_space);
        indentSpace.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 50));
        indentSpace.setBackground(getDrawable(R.drawable.recipe_instruction_border_1));

        //ingredients title
        TextView ingredientsTitle = (TextView) findViewById(R.id.ingredientsTitle);
        ingredientsTitle.setTextColor(Color.BLACK);

        // ingredients
        Intent intent = getIntent();
        String ingredientString = intent.getStringExtra("ingredients");
        TextView ingredients = findViewById(R.id.ingredients_text);
        ingredients.setBackgroundColor(Color.WHITE);
        ingredients.setPadding(40, 75, 40, 0);
        ingredients.setTextColor(Color.BLACK);
        ingredients.setText(ingredientString);

        //Equipment title
        TextView equipmentTitle = (TextView) findViewById(R.id.equipmentTitle);
        equipmentTitle.setTextColor(Color.BLACK);

        //equipment
        String equipmentString = intent.getStringExtra("equipment");
        TextView equipment = findViewById(R.id.equipment_text);
        equipment.setBackgroundColor(Color.WHITE);
        equipment.setPadding(40, 75, 40, 50);
        equipment.setTextColor(Color.BLACK);
        equipment.setText(equipmentString);

        // Line after ingredients lsit
        TextView indentSpace3 = findViewById(R.id.indent_space3);
        indentSpace3.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 50));
        indentSpace3.setBackground(getDrawable(R.drawable.recipe_instruction_border_1));

        // Line after ingredients lsit
        TextView indentSpace2 = findViewById(R.id.indent_space2);
        indentSpace2.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 50));
        indentSpace2.setBackground(getDrawable(R.drawable.recipe_instruction_border_1));

        //instructions title
        TextView instructionsTitle = (TextView) findViewById(R.id.instructionsTitle);
        instructionsTitle.setTextColor(Color.BLACK);

        // instructions
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
