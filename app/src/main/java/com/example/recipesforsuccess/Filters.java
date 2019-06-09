package com.example.recipesforsuccess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;


import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;

public class Filters extends AppCompatActivity {
    //    LinearLayout mainDisplayS
    Intent intent = new Intent();
    ArrayList<String> intolerances = new ArrayList();
    ArrayList<String> diet = new ArrayList();
    ArrayList<String> type = new ArrayList();
    ArrayList<String> cuisine = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        Button confirm = (Button) findViewById(R.id.confirmFilters);

        confirm.setOnClickListener(new View.OnClickListener() {
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
        final String[] arr = {"eggs", "treenuts", "dairy", "wheat", "gluten", "peanut",
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
                intolerances.add(options[i]);
                box[i].setChecked(true);
            }else{
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

    /*public void onCheckboxClicked(View v) {
        //code to check if this checkbox is checked!

        boolean checked = ((CheckBox) v).isChecked();

        CheckBox eggCheck = (CheckBox) v.findViewById(R.id.eggs);
        /*switch(v.getId()){
            case R.id.eggs:
                if (checked){
                    intolerances.add("egg");
                    System.out.println("PUTTING IN EGGS BOI");
                }
                else {
                    intolerances.remove("egg");
                    System.out.println("TAKING AWAY THE EGGS");
                }
                break;
            case R.id.treenuts:
                if (checked){
                    intolerances.add("tree+nut");
                    System.out.println("PUTTING IN treenuts BOI");
                }
                else {
                    intolerances.remove("tree+nut");
                    System.out.println("TAKING AWAY THE treenuts");
                }
                break;
            case R.id.dairy:
                if (checked) {
                    intolerances.add("dairy");
                    System.out.println("PUTTING IN dairy BOI");
                }
                else {
                    intolerances.remove("dairy");
                    System.out.println("TAKING AWAY THE dairy");
                }
                break;
            case R.id.gluten:
                if (checked) {
                    intolerances.add("gluten");
                    System.out.println("PUTTING IN gluten BOI");
                }
                else {
                    intolerances.remove("gluten");
                    System.out.println("TAKING AWAY THE gluten");
                }
                break;
            case R.id.peanut:
                if (checked) {
                    intolerances.add("peanut");
                    System.out.println("PUTTING IN peanut BOI");
                }
                else {
                    intolerances.remove("peanut");
                    System.out.println("TAKING AWAY THE peanut");
                }
                break;
            case R.id.sesame:
                if (checked) {
                    intolerances.add("sesame");
                    System.out.println("PUTTING IN sesame BOI");
                }
                else {
                    intolerances.remove("sesame");
                    System.out.println("TAKING AWAY THE sesame");
                }
                break;
            case R.id.shellFish:
                if (checked) {
                    intolerances.add("shellfish");
                    System.out.println("PUTTING IN shellFish BOI");
                }
                else {
                    intolerances.remove("shellfish");
                    System.out.println("TAKING AWAY THE NUTS");
                }
                break;
            case R.id.soy:
                if (checked) {
                    intolerances.add("soy");
                    System.out.println("PUTTING IN soy BOI");
                }
                else {
                    intolerances.remove("soy");
                    System.out.println("TAKING AWAY THE soy");
                }
                break;
            case R.id.wheat:
                if (checked) {
                    intolerances.add("wheat");
                    System.out.println("PUTTING IN wheat BOI");
                }
                else {
                    intolerances.remove("wheat");
                    System.out.println("TAKING AWAY THE wheat");
                }
                break;
            case R.id.seafood:
                if (checked) {
                    intolerances.add("seafood");
                    System.out.println("PUTTING IN seafood BOI");
                }
                else {
                    intolerances.remove("seafood");
                    System.out.println("TAKING AWAY THE seafood");
                }
                break;
            case R.id.vegan:
                if (checked) {
                    diet.add("vegan");
                    System.out.println("Adding vegan option");
                }
                else {
                    diet.remove("vegan");
                    System.out.println("Removing vegan option");
                }
                break;
            case R.id.vegetarian:
                if (checked) {
                    diet.add("vegetarian");
                    System.out.println("Adding vegetarian option");
                }
                else {
                    diet.remove("vegetarian");
                    System.out.println("Removing vegetarian option");
                }
                break;
            case R.id.pescetarian:
                if (checked) {
                    diet.add("pescetarian");
                    System.out.println("Adding pescetarian option");
                }
                else {
                    diet.remove("pescetarian");
                    System.out.println("Removing pescetarian option");
                }
                break;
            case R.id.mainCourse:
                if (checked) {
                    type.add("main+course");
                    System.out.println("Adding main course option");
                }
                else {
                    type.remove("main+course");
                    System.out.println("Removing main course option");
                }
                break;
            case R.id.dessert:
                if (checked) {
                    type.add("dessert");
                    System.out.println("Adding dessert option");
                }
                else {
                    type.remove("dessert");
                    System.out.println("Removing dessert option");
                }
                break;
            case R.id.appetizer:
                if (checked) {
                    type.add("appetizer");
                    System.out.println("Adding appetizer option");
                }
                else {
                    type.remove("appetizer");
                    System.out.println("Removing appetizer option");
                }
                break;
            case R.id.salad:
                if (checked) {
                    type.add("salad");
                    System.out.println("Adding salad option");
                }
                else {
                    type.remove("salad");
                    System.out.println("Removing salad option");
                }
                break;
            case R.id.breakfast:
                if (checked) {
                    type.add("breakfast");
                    System.out.println("Adding breakfast option");
                }
                else {
                    type.remove("breakfast");
                    System.out.println("Removing breakfast option");
                }
                break;
            case R.id.drink:
                if (checked) {
                    type.add("drink");
                    System.out.println("Adding drink option");
                }
                else {
                    type.remove("drink");
                    System.out.println("Removing drink option");
                }
                break;
            case R.id.soup:
                if (checked) {
                    type.add("soup");
                    System.out.println("Adding soup option");
                }
                else {
                    type.remove("soup");
                    System.out.println("Removing soup option");
                }
                break;
            case R.id.bread:
                if (checked) {
                    type.add("bread");
                    System.out.println("Adding bread option");
                }
                else {
                    type.remove("bread");
                    System.out.println("Removing bread option");
                }
                break;
            case R.id.american:
                if (checked) {
                    cuisine.add("american");
                    System.out.println("Adding american option");
                }
                else {
                    cuisine.remove("american");
                    System.out.println("american american option");
                }
                break;
            case R.id.italian:
                if (checked) {
                    cuisine.add("italian");
                    System.out.println("Adding italian option");
                }
                else {
                    cuisine.remove("italian");
                    System.out.println("american italian option");
                }
                break;
            case R.id.mexican:
                if (checked) {
                    cuisine.add("mexican");
                    System.out.println("Adding mexican option");
                }
                else {
                    cuisine.remove("mexican");
                    System.out.println("american mexican option");
                }
                break;
            case R.id.indian:
                if (checked) {
                    cuisine.add("indian");
                    System.out.println("Adding indian option");
                }
                else {
                    cuisine.remove("indian");
                    System.out.println("adding indian option");
                }
                break;
            case R.id.chinese:
                if (checked) {
                    cuisine.add("chinese");
                    System.out.println("Adding chinese option");
                }
                else {
                    cuisine.remove("chinese");
                    System.out.println("american chinese option");
                }
                break;

            default:
                break;
        }
    }*/


}