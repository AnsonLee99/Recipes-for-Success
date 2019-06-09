package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ViewRecipeInstructions extends AppCompatActivity {
    private Button pushToShopping;
    private Button pullFromBasket;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe_instructions);

        pushToShopping = (Button) findViewById(R.id.ingredient_to_shopping);
        pullFromBasket = (Button) findViewById(R.id.ingredient_from_basket);

        if ( getIntent().getStringExtra("missingIngredients") == null ||
                getIntent().getStringExtra("missingIngredients") == "") {
            pushToShopping.setVisibility(View.GONE);
        }
        else {
            pushToShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] names = getIntent().getStringExtra("missingIngredients").split(";");

                    for (String name : names ) {
                        name += "_" + getIntent().getStringExtra("ID");

                        db.collection("USERS").document(getIntent().getStringExtra("ID")).update("shoppingList",
                                FieldValue.arrayUnion(name));
                    }
                }
            });
        }

        if ( getIntent().getStringExtra("usedIngredients") == null ||
                getIntent().getStringExtra("usedIngredients") == "") {
            pullFromBasket.setVisibility(View.GONE);
        }
        else {
            pullFromBasket.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String[] names = getIntent().getStringExtra("usedIngredients").split(";");

                    for (String name: names) {
                        name += "_" + getIntent().getStringExtra("ID");

                        db.collection("USERS").document(getIntent().getStringExtra("ID")).update("basket",
                                FieldValue.arrayRemove(name));
                    }
                }
            });
        }

        // Hide action bar
        getSupportActionBar().hide();

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

        // Prep Time
        TextView prepTime = findViewById(R.id.prepTime);
        prepTime.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        prepTime.setText("Prep Time: " + getIntent().getIntExtra("prepTime",0) +
                " minutes");
        prepTime.setTextSize(15);
        prepTime.setTextColor(Color.BLACK);
        prepTime.setGravity(Gravity.START);
        prepTime.setBackgroundColor(Color.WHITE);
        prepTime.setPadding(50,30,0,30);

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

        //add to shopping list button
        Intent toRecipe = new Intent(this, GroceryList.class);

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
