package com.example.recipesforsuccess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;

public class Filters extends AppCompatActivity {
    //    LinearLayout mainDisplayS
    Intent intent = new Intent();
    ArrayList<String> intolerances = new ArrayList();
    ArrayList<String> diet = new ArrayList();
    ArrayList<String> type = new ArrayList();
    ArrayList<String> cuisine = new ArrayList();

    private Boolean isEggsChecked;
    private Boolean isVeganChecked;
    private Boolean isDessertChecked;

    public CheckBox eggs;
    public CheckBox vegan;
    public CheckBox dessert;
    private Button apply;



    public static final String SHARED_PREFS = "sharedPrefs";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);
        apply = (Button) findViewById(R.id.confirmFilters);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("intolerances", intolerances);
                intent.putExtra("diet", diet);
                intent.putExtra("type", type);
                intent.putExtra("cuisine", cuisine);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        eggs = (CheckBox) findViewById(R.id.eggs);
        eggs.setChecked(false);
        System.out.println("ONCREATE is eggs checked: " + eggs.isChecked());
        //eggs.setTextColor(Color.RED);


        System.out.println("ENTERING FILTERS INTENT");
        final CheckBox eggs = (CheckBox)findViewById(R.id.eggs);
        final CheckBox treenuts = (CheckBox)findViewById(R.id.treenuts);
        final CheckBox dairy= (CheckBox)findViewById(R.id.dairy);
        final CheckBox wheat = (CheckBox)findViewById(R.id.wheat);
        final CheckBox gluten = (CheckBox)findViewById(R.id.gluten);
        final CheckBox peanut = (CheckBox)findViewById(R.id.peanut);
        final CheckBox sesame = (CheckBox)findViewById(R.id.sesame);
        final CheckBox soy = (CheckBox)findViewById(R.id.soy);
        final CheckBox shellFish = (CheckBox)findViewById(R.id.shellFish);
        final CheckBox seafood = (CheckBox)findViewById(R.id.seafood);
        final CheckBox vegetarian = (CheckBox)findViewById(R.id.vegetarian);
        final CheckBox vegan = (CheckBox)findViewById(R.id.vegan);
        final CheckBox pescetarian = (CheckBox)findViewById(R.id.pescetarian);
        final CheckBox dessert = (CheckBox)findViewById(R.id.dessert);
        final CheckBox appetizer = (CheckBox)findViewById(R.id.appetizer);
        final CheckBox salad = (CheckBox)findViewById(R.id.salad);
        final CheckBox drink = (CheckBox)findViewById(R.id.drink);
        final CheckBox breakfast = (CheckBox)findViewById(R.id.breakfast);
        final CheckBox soup = (CheckBox)findViewById(R.id.soup);
        final CheckBox bread = (CheckBox)findViewById(R.id.bread);
        final CheckBox american = (CheckBox)findViewById(R.id.american);
        final CheckBox chinese = (CheckBox)findViewById(R.id.chinese);
        final CheckBox italian = (CheckBox)findViewById(R.id.italian);
        final CheckBox mexican = (CheckBox)findViewById(R.id.mexican);
        final CheckBox indian = (CheckBox)findViewById(R.id.indian);
        final CheckBox[] boxes = {eggs, treenuts, dairy, wheat, gluten, peanut,
                                sesame, soy, shellFish, seafood, vegetarian, vegan,
                            pescetarian, dessert, appetizer, salad, drink, breakfast,
                            soup, bread, american, chinese, italian, mexican, indian};
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        final String[] arr = {"egg", "tree+nut", "dairy", "wheat", "gluten", "peanut",
                                "sesame", "soy", "shellFish", "seafood", "vegetarian", "vegan",
                                    "pescetarian", "dessert", "appetizer", "salad", "drink",
                                    "breakfast", "soup", "bread", "american", "chinese",
                                    "italian", "mexican", "indian"};
        setPreferences(preferences, arr, boxes, editor);
    }

    public void setPreferences(SharedPreferences preferences, final String[] options, final CheckBox[] box,
                                final SharedPreferences.Editor editor){
        for(int i = 0; i < options.length; i++){
            if(preferences.getBoolean(options[i], false) == true){
                if(i < 10){
                    intolerances.add(options[i]);
                }
                else if (i < 13){
                    diet.add(options[i]);
                }
                else if (i < 20){
                    type.add(options[i]);
                }
                else{
                    cuisine.add(options[i]);
                }
                box[i].setChecked(true);
            }
            else{
                box[i].setChecked(false);
            }
            final int ii = i;
            box[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(box[ii].isChecked()){
                        editor.putBoolean(options[ii], true);
                    }else{
                        editor.putBoolean(options[ii], false);
                    }
                    editor.apply();
                }
            });
        }
    }
}