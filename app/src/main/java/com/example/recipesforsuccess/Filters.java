package com.example.recipesforsuccess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Toast;


import java.util.ArrayList;

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
        //Checkboxes
        System.out.println("finding eggs");
//        if( eggs == null ){
//            System.out.println("fucking null eggss");
//        }else{
//            System.out.println("good eggs");
//        }


        setContentView(R.layout.activity_filters);
        apply = (Button) findViewById(R.id.confirmFilters);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
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
        loadData();
        updateViews();


        System.out.println("ENTERING FILTERS INTENT");
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        System.out.println("saving data, eggsischecked is: " + eggs.isChecked());
        editor.putBoolean("eggs", eggs.isChecked());
        //editor.putBoolean("Vegan", eggs.isChecked());
        //editor.putBoolean("Soy", eggs.isChecked());

        editor.apply();
        System.out.println("IS EGGS CHECKED?" + eggs.isChecked());

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public boolean test(SharedPreferences sp){
        return sp.getBoolean("eggs", false);
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        System.out.println("sharedpref for soy " + sharedPreferences.getBoolean("soy", false));
        isEggsChecked = sharedPreferences.getBoolean("eggs", false);
        //isVeganChecked = sharedPreferences.getBoolean("Vegan", false);
        //isDessertChecked = sharedPreferences.getBoolean("Dessert", false);


        System.out.println("loaded data iseggschecked...: " + test(sharedPreferences));
    }

    public void updateViews(){
        eggs.setChecked(isEggsChecked);
        //vegan.setChecked(isVeganChecked);
        //dessert.setChecked(isDessertChecked);


        Log.d("test", "THIS IS THE BOOL " + isEggsChecked);
    }

    public void onCheckboxClicked(View v) {
        //code to check if this checkbox is checked!
        boolean checked = ((CheckBox) v).isChecked();

        eggs = (CheckBox) v.findViewById(R.id.eggs);
        //vegan = (CheckBox) v.findViewById(R.id.eggs);
        //eggs = (CheckBox) v.findViewById(R.id.eggs);


        System.out.println("inside oncheckbox clicked");
//        if( eggs == null ){
//            System.out.println("fucking null eggss");
//        }else{
//            System.out.println("good eggs");
//        }
//        if (v.getId() == R.id.eggs){
//            Log.d("test", "EGGS IS TRUE");
//        }
//        else{
//            Log.d("test", "EGGS IS FALSE ");
//        }
        switch(v.getId()){
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
    }


}