package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.app.SearchManager;
import android.widget.SearchView;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Space;
import android.widget.TextView;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.*;

import javax.annotation.Nullable;


public class Recipes extends MainPage {
    LinearLayout mainDisplay;
    String USER_QUERY = "";
    String SPOONACULAR_API_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?&instructionsRequired=true&query=";
    String dataParsed = "";
    String SPOONACULAR_IMAGE_URI = "https://spoonacular.com/recipeImages/";

    final int SEARCH_LAYOUT_WIDTH = 1000;
    final int SEARCH_LAYOUT_HEIGHT = 500;
    final int SEARCH_IMG_SIZE = 300;
    final int SEARCH_TEXT_WIDTH = 700;
    final int SEARCH_TEXT_HEIGHT = 300;

    final int RECIPE_LAYOUT_WIDTH = 1200;
    final int RECIPE_LAYOUT_HEIGHT = 500;
    final int RECIPE_IMG_SIZE = 800;
    final int REcIPE_TEXT_WIDTH = 700;
    final int RECIPE_TEXT_HEIGHT = 440;

    int SECOND_ACTIVITY_REQUEST_CODE = 0;

    ArrayList<String> intolerances = new ArrayList();
    ArrayList<String> diets = new ArrayList();
    ArrayList<String> categories = new ArrayList();
    ArrayList<String> cuisine = new ArrayList();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth currAuth = this.passAuth();
    private final FirebaseUser user = currAuth.getCurrentUser();
    private String userID = user.getUid();

    List<String> userBasket;
    List<String> recipeIDs;;
    FloatingActionButton createRecipeButton;

    private CollectionReference recipeRef = db.collection("RECIPES");
    private DocumentReference currentUser = db.collection("USERS").document(userID);
    LinearLayout personalRecipes;
    private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
    RecipeAdapter adapter;
    Context context;

    int id = 0;
    int tracker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_recipes, null);
        mainDisplay.addView(groceryView);
        context = this;

        createRecipeButton = (FloatingActionButton) findViewById(R.id.createRecipeButton);
        createRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recipes.this, CreateRecipe.class));
            }
        });

        setSelected(1);

        adapter = new RecipeAdapter(context, recipes);
        personalRecipes = (LinearLayout)findViewById(R.id.personalRecipes);


        currentUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    recipeIDs = (List<String>) documentSnapshot.get("personalRecipes");
                    if(personalRecipes.getChildCount() > 0){
                        personalRecipes.removeAllViews();
                    }
                    new GetPersonalRecipes().execute();
                }
            }
        });

        DocumentReference userDoc = db.document(("USERS/" + userID));
        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if( documentSnapshot.exists() ){
                    userBasket = (List<String>) documentSnapshot.get("basket");
                    for(int i = 0; i < userBasket.size(); i++){
                    }
                    new GetRecipeByIngredients().execute();
                }
            }
        });

        handleIntent(getIntent());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get SearchView and set configuration from /res/xml/searchable.xml
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        ImageButton filtersButton = (ImageButton) findViewById(R.id.filters);

        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiltersPage();
                Intent intent = getIntent();
            }
        });
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                intolerances = data.getStringArrayListExtra("intolerances");
                diets = data.getStringArrayListExtra("diet");
                categories = data.getStringArrayListExtra("type");
                cuisine = data.getStringArrayListExtra("cuisine");
            }
        }
    }

    public void openFiltersPage(){
        Intent intent = new Intent(this, Filters.class);
        startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handles search queries when user clicks on search button after entering text
     * @param intent
     */
    private void handleIntent(Intent intent){
        if( Intent.ACTION_SEARCH.equals(intent.getAction()) ){
            //setContentView(R.layout.activity_search_results);
            USER_QUERY = intent.getStringExtra(SearchManager.QUERY);
            new RecipeSearch().execute();
            mainDisplay.removeAllViews();
            mainDisplay = (LinearLayout) findViewById(R.id.main_display);
            View recipeSearchView = getLayoutInflater().inflate(R.layout.activity_recipe_search, null);
            mainDisplay.addView(recipeSearchView);
            //setContentView(R.layout.activity_recipe_search);
        }

    }

    class GetPersonalRecipes extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            for(int i = 0; i < recipeIDs.size(); i++)
            {
                String ID = recipeIDs.get(i);
                //private DocumentReference currentUser = db.collection("USERS").document(userID);
                DocumentReference recRef = db.document("RECIPES/" + ID);
                recRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists())
                        {
                            Recipe currRecipe = documentSnapshot.toObject(Recipe.class);
                            personalRecipes.addView(insertPersonalIMG(currRecipe, RECIPE_LAYOUT_WIDTH,
                                    RECIPE_LAYOUT_HEIGHT,RECIPE_IMG_SIZE,REcIPE_TEXT_WIDTH,RECIPE_TEXT_HEIGHT));
                        }
                    }
                });
            }
            return "";
        }
    }

    class GetRecipeByIngredients extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String base_url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients?";
            String numSearch = "number=5";
            String maxUsed = "&ranking=1&ignorePantry=false"; // Maximize used ingredients
            String minMissing = "&ranking=2&ignorePantry=false"; // Minimize missing ingredient
            String ingredients = "&ingredients=";


            for(int ingInd = 0; ingInd < userBasket.size(); ingInd++){
                String finalIng = "";
                String[] ingArray = userBasket.get(ingInd).split(" ");
                if( ingArray.length != 1 ){
                    for(int ii = 0; ii < ingArray.length; ii++) {
                        if( ii == ingArray.length - 1 && ingInd == userBasket.size() - 1) { // last item
                            finalIng += (ingArray[ii]);
                        }else if( ii == ingArray.length - 1 && ingInd != userBasket.size() - 1) {
                            finalIng += (ingArray[ii] + "+");
                        }else{
                            finalIng += (ingArray[ii] + "+");
                        }
                    }
                }else{
                    finalIng = userBasket.get(ingInd).split("_")[0];
                }
                if(ingInd == userBasket.size() - 1) {
                    ingredients += finalIng;
                }else{
                    ingredients += (finalIng + "%2C+");
                }

            }
            String apiURL = base_url + numSearch + maxUsed + ingredients;
            try {
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                return null;
            }
        }

        protected void onPostExecute(String response){
            try{
                String instructions;

                // Remove all current photos on new search
                LinearLayout photos = (LinearLayout) findViewById(R.id.recommended_recipes);
                if( photos.getChildCount() > 0){
                    photos.removeAllViews();
                }
                int numToRetrieve = 5;

                JSONArray jsonArray = new JSONArray(response);
                for(int xx = 0; xx < numToRetrieve; xx++) {
                    dataParsed = "";
                    JSONObject hit = jsonArray.getJSONObject(xx);
                    id = hit.getInt("id");

                    String imgURL = hit.getString("image");
                    String foodName = hit.getString("title");
                    //String prepTime = hit.getString("readyInMinutes");
                    photos.addView(insertIMG(imgURL, foodName, "", hit, 1, RECIPE_LAYOUT_WIDTH,
                            RECIPE_LAYOUT_HEIGHT,RECIPE_IMG_SIZE,REcIPE_TEXT_WIDTH,RECIPE_TEXT_HEIGHT));
                }

            }catch(JSONException e){
            }
        }
    }

    class GetRecipeEquipment extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String equipmentURLString = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + id + "/equipmentWidget.json";

            try {
                URL url = new URL(equipmentURLString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                return null;
            }
        }

        protected void onPostExecute(String response){
            super.onPostExecute(response);
        }
    }


    // Background thread used to retrieve data from API
    class RecipeSearch extends AsyncTask<Void, Void, String>{


        private Exception exception;

        // Step used to set up retrieving data from API
        protected void onPreExecute(){

        }

        // Retrieves API data using user query
        protected String doInBackground(Void... urls) {

            try {
                String urlString = SPOONACULAR_API_URL + USER_QUERY;

                if (!intolerances.isEmpty()){
                    urlString = appendOptionToURL(intolerances, urlString, "intolerances");
                }
                if (!diets.isEmpty()){
                    urlString = appendOptionToURL(diets, urlString, "diet");
                }
                if (!categories.isEmpty()){
                    urlString = appendOptionToURL(categories, urlString, "type");
                }
                if (!cuisine.isEmpty()){
                    urlString = appendOptionToURL(cuisine, urlString, "cuisine");
                }
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "345c2d3917msh6a48dea5f64bfa4p1a6cd4jsn7ead3be73465");

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {

                return null;
            }

        }

        public String appendOptionToURL(ArrayList<String> options, String urlString, String parameter){
            urlString = urlString +  "&" + parameter + "=" + options.get(0);
            for(int i = 1; i < options.size(); i++){
                urlString = urlString + "%2C" + options.get(i);
            }
            return urlString;
        }

        // Get result from API and return result back to main UI thread
        protected void onPostExecute(String response){
            if(response == null){
                response = "There was an error";
            }

            try{
                // Remove all current photos on new search
                LinearLayout photos = (LinearLayout) findViewById(R.id.searched_recipes);
                if( photos.getChildCount() > 0){
                    photos.removeAllViews();
                }
                int numToRetrieve = 5;
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int xx = 0; xx < numToRetrieve; xx++) {
                    dataParsed = "";
                    JSONObject hit = jsonArray.getJSONObject(xx);
                    String imgURL = SPOONACULAR_IMAGE_URI + hit.getString("image");
                    String foodName = hit.getString("title");
                    String prepTime = hit.getString("readyInMinutes");

                    //parsing ingredients

                    photos.addView(insertIMG(imgURL, foodName, prepTime, hit, 0, SEARCH_LAYOUT_WIDTH,
                            SEARCH_LAYOUT_HEIGHT,SEARCH_IMG_SIZE,SEARCH_TEXT_WIDTH,SEARCH_TEXT_HEIGHT));
                }



            }catch(JSONException e){}
        }
    }

    class RecipeInstructions extends AsyncTask<Void, Void, String> {
        String instructions_call = "";

        @Override
        protected String doInBackground(Void... voids) {
            try{
                String url_str = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + id + "/information";
                URL url = new URL(url_str);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                httpURLConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    instructions_call = instructions_call + line;
                }

                return instructions_call;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    View insertIMG(final String imgURL, final String foodName, final String prepTime, final JSONObject hit, final int task,
                    final int layoutWidth, final int layoutHeight, final int imageSize, final int textWidth, final int textHeight){

        LinearLayout layout = new LinearLayout(getApplicationContext());
        ImageButton recipeIMG = new ImageButton(getApplicationContext());

        TextView recipeName = new TextView(getApplicationContext());
        recipeName.setLayoutParams(new LinearLayout.LayoutParams(textWidth, textHeight));

        LinearLayout pictureTextCombo = new LinearLayout(getApplicationContext());
        pictureTextCombo.setOrientation(LinearLayout.VERTICAL);
        pictureTextCombo.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth - 200, layoutHeight + 250));
        pictureTextCombo.setPadding(0,45,0,0);

        if(task == 0) {
            layout.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth, layoutHeight));
            recipeIMG.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
            recipeName.setPadding(75, 50, 0, 0);
        } else {
            layout.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth - 200, layoutHeight + 250));
            recipeIMG.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize - 300));
            recipeName.setPadding(50,0,0,0);
            recipeName.setTextSize(15);
            recipeName.setGravity(Gravity.TOP);
        }


        recipeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                String finalImgURL = "";
                String recipeName = "";
                String ingredientList = "";
                String equipmentList = "";

                try {
                    //JSONObject recipe = hit1.getJSONObject("results");
                    recipeName = hit.getString("title");


                    id = hit.getInt("id");

                    finalImgURL = imgURL;

                    String recipeInfo = "";
                    String equipmentInfo = "";
                    recipeInfo = new RecipeInstructions().execute().get();
                    equipmentInfo = new GetRecipeEquipment().execute().get();

                    //For instructions
                    JSONObject recipeSearch = new JSONObject(recipeInfo);

                    //getting prep time and servings
                    int prepTime = recipeSearch.getInt("readyInMinutes");

                    JSONArray analyzedInstructions = recipeSearch.getJSONArray("analyzedInstructions");
                    JSONObject instructionsArray;
                    JSONArray stepsArray;
                    if( !analyzedInstructions.isNull(0) ){

                        instructionsArray = analyzedInstructions.getJSONObject(0);
                        stepsArray = instructionsArray.getJSONArray("steps");
                    }else{
                        instructionsArray = null;
                        stepsArray = null;
                    }

                    //For equipment
                    JSONObject equipmentSearch = new JSONObject(equipmentInfo);
                    JSONArray equipmentArray = equipmentSearch.getJSONArray("equipment");


                    //getting ingredients list
                    JSONArray extendedIngredients = recipeSearch.getJSONArray("extendedIngredients");
                    for (int ii = 0; ii < extendedIngredients.length(); ii++){
                        JSONObject ingredient = extendedIngredients.getJSONObject(ii);
                        String original = ingredient.getString("original");
                        ingredientList = ingredientList + (ii + 1) + ".   " + "" + original + "\n" + "" + "\n";
                    };

                    //getting equipment list
                    for (int ii = 0; ii < equipmentArray.length(); ii++){
                        JSONObject equipment = equipmentArray.getJSONObject(ii);
                        String name = equipment.getString("name");
                        equipmentList = equipmentList + (ii + 1) + ".   " + "" + name + "\n" + "" + "\n";
                    }

                    // Get missing ingredient list
                    StringBuilder missingIngredients = new StringBuilder("");
                    if(task == 1) {
                        JSONArray missedIngredients = (JSONArray) hit.get("missedIngredients");
                        if (missedIngredients != null) {
                            for (int j = 0; j < missedIngredients.length(); j++) {
                                missingIngredients.append(missedIngredients.getJSONObject(j).get("name") + ";");
                            }
                        } else {
                            missingIngredients.append("");
                        }
                    }

                    // Get used ingredients list
                    StringBuilder usedIngredients = new StringBuilder("");
                    if(task == 1) {
                        JSONArray JObjectUsed = (JSONArray) hit.get("usedIngredients");
                        if (usedIngredients != null) {
                            for (int j = 0; j < JObjectUsed.length(); j++) {
                                usedIngredients.append(JObjectUsed.getJSONObject(j).get("name") + ";");
                            }
                        } else {
                            usedIngredients.append("");
                        }
                    }

                    dataParsed = "";
                    if( stepsArray != null ) {
                        for (int ii = 0; ii < stepsArray.length(); ii++) {
                            JSONObject step = stepsArray.getJSONObject(ii);
                            int number = step.getInt("number");
                            String stepInfo = step.getString("step");
                            dataParsed = dataParsed + number + ".   " + "" + stepInfo + "\n" + "" +
                                    "\n";
                        }
                    }

                    intent.putExtra("ingredients", ingredientList);
                    intent.putExtra("equipment", equipmentList);
                    intent.putExtra("instructions", dataParsed);
                    intent.putExtra("imgURL", finalImgURL);
                    intent.putExtra("recipeName", recipeName);
                    intent.putExtra("missingIngredients", missingIngredients.toString());
                    intent.putExtra("usedIngredients", usedIngredients.toString());
                    intent.putExtra("ID", userID);
                    intent.putExtra("prepTime", prepTime);

                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        });

        recipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                String finalImgURL = "";
                String recipeName = "";
                String ingredientList = "";
                String equipmentList = "";

                try {
                    recipeName = hit.getString("title");


                    id = hit.getInt("id");

                    finalImgURL = imgURL;

                    String recipeInfo = "";
                    String equipmentInfo = "";
                    recipeInfo = new RecipeInstructions().execute().get();
                    equipmentInfo = new GetRecipeEquipment().execute().get();


                    //For instructions
                    JSONObject recipeSearch = new JSONObject(recipeInfo);
                    JSONArray analyzedInstructions = recipeSearch.getJSONArray("analyzedInstructions");

                    //getting prep time and servings
                    int prepTime = recipeSearch.getInt("readyInMinutes");

                    JSONObject instructionsArray;
                    JSONArray stepsArray;
                    if( !analyzedInstructions.isNull(0) ){
                        instructionsArray = analyzedInstructions.getJSONObject(0);
                        stepsArray = instructionsArray.getJSONArray("steps");
                    }else{
                        instructionsArray = null;
                        stepsArray = null;
                    }

                    //For equipment
                    JSONObject equipmentSearch = new JSONObject(equipmentInfo);
                    JSONArray equipmentArray = equipmentSearch.getJSONArray("equipment");


                    //getting ingredients list
                    JSONArray extendedIngredients = recipeSearch.getJSONArray("extendedIngredients");
                    for (int ii = 0; ii < extendedIngredients.length(); ii++){
                        JSONObject ingredient = extendedIngredients.getJSONObject(ii);
                        String original = ingredient.getString("original");
                        ingredientList = ingredientList + (ii + 1) + ".   " + "" + original + "\n" + "" + "\n";
                    };

                    //getting equipment list
                    for (int ii = 0; ii < equipmentArray.length(); ii++){
                        JSONObject equipment = equipmentArray.getJSONObject(ii);
                        String name = equipment.getString("name");
                        equipmentList = equipmentList + (ii + 1) + ".   " + "" + name + "\n" + "" + "\n";
                    };
                    dataParsed = "";
                    if(stepsArray != null) {
                        for (int ii = 0; ii < stepsArray.length(); ii++) {
                            JSONObject step = stepsArray.getJSONObject(ii);
                            int number = step.getInt("number");
                            String stepInfo = step.getString("step");
                            dataParsed = dataParsed + number + ".   " + "" + stepInfo + "\n" + "" +
                                    "\n";
                        }
                    }

                    intent.putExtra("ingredients", ingredientList);
                    intent.putExtra("equipment", equipmentList);
                    intent.putExtra("instructions", dataParsed);
                    intent.putExtra("imgURL", finalImgURL);
                    intent.putExtra("recipeName", recipeName);
                    intent.putExtra("prepTime", prepTime);

                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        });


        Picasso.with(getApplicationContext()).load(imgURL).into(recipeIMG);
        recipeIMG.setScaleType(ImageView.ScaleType.FIT_XY);
        recipeIMG.setBackground(getDrawable(R.drawable.recipe_scroll_viewer));

        String boldedFoodName = "<b>" + foodName + "</b>";
        recipeName.setText(Html.fromHtml(boldedFoodName));

        if(task == 0) {
            recipeName.append("\nPrep Time: " + prepTime + " min");
        }

        recipeName.setTextColor(Color.BLACK);

        if(task == 0) {
            layout.setPadding(0, 100, 0, 0);
        }

        if( task == 1 ) {

            pictureTextCombo.addView(recipeIMG);
            pictureTextCombo.addView(recipeName);
            layout.addView(pictureTextCombo);

        } else {

            layout.addView(recipeIMG);
            layout.addView(recipeName);
        }

        return layout;
    }

    View insertPersonalIMG(final Recipe myRecipe, final int layoutWidth, final int layoutHeight, final int imageSize, final int textWidth, final int textHeight){

        final String prepTime = myRecipe.getPrepTime();
        LinearLayout layout = new LinearLayout(getApplicationContext());
        ImageButton recipeIMG = new ImageButton(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth - 200, layoutHeight + 250));
        recipeIMG.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize - 300));
        TextView recipeName = new TextView(getApplicationContext());
        recipeName.setLayoutParams(new LinearLayout.LayoutParams(textWidth, textHeight));
        recipeName.setPadding(50,0,0,0);
        recipeName.setTextSize(15);
        recipeName.setGravity(Gravity.TOP);

        LinearLayout pictureTextCombo = new LinearLayout(getApplicationContext());
        pictureTextCombo.setOrientation(LinearLayout.VERTICAL);
        pictureTextCombo.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth - 200, layoutHeight + 250));
        pictureTextCombo.setPadding(0,45,0,0);


        recipeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                String finalImgURL = myRecipe.getRecipePic();
                String recipeName = myRecipe.getName();
                List<String> ingredientList = myRecipe.getIngredients();
                List<String> steps = myRecipe.getSteps();
                List<String> equipment = myRecipe.getEquipment();


                String equipmentList = "";
                for(int i = 0; i < equipment.size(); i++)
                {
                    equipmentList = equipmentList + (i+1) + ".  " + "" + equipment.get(i) + "\n" + "" + "\n";
                }

                String ingredients = "";
                for(int i = 0; i < ingredientList.size(); i++){
                    ingredients = ingredients + (i+1) + ".  " + "" + ingredientList.get(i) + "\n" + "" + "\n";
                }

                String dataParsed = "";
                for(int i = 0; i < steps.size(); i++){
                    dataParsed = dataParsed + (i+1) + ".  " + "" + steps.get(i) + "\n" + "" + "\n";
                }

                intent.putExtra("ingredients", ingredients);
                intent.putExtra("equipment", equipmentList);
                intent.putExtra("instructions", dataParsed);
                intent.putExtra("imgURL", finalImgURL);
                intent.putExtra("recipeName", recipeName);
                intent.putExtra("prepTime", prepTime);
                //intent.putExtra("servings", servings);

                startActivity(intent);

            }
        });

        recipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                String finalImgURL = myRecipe.getRecipePic();
                String recipeName = myRecipe.getName();
                List<String> ingredientList = myRecipe.getIngredients();
                List<String> steps = myRecipe.getSteps();
                List<String> equipment = myRecipe.getEquipment();


                String equipmentList = "";
                for(int i = 0; i < equipment.size(); i++) {
                    equipmentList = equipmentList + (i + 1) + ".  " + "" + equipment.get(i) + "\n" + "" + "\n";
                }

                String ingredients = "";
                for(int i = 0; i < ingredientList.size(); i++){
                    ingredients = ingredients + (i+1) + ".  " + "" + ingredientList.get(i) + "\n" + "" + "\n";
                }

                String dataParsed = "";
                for(int i = 0; i < steps.size(); i++){
                    dataParsed = dataParsed + (i+1) + ".  " + "" + steps.get(i) + "\n" + "" + "\n";
                }

                intent.putExtra("ingredients", ingredients);
                intent.putExtra("equipment", equipmentList);
                intent.putExtra("instructions", dataParsed);
                intent.putExtra("imgURL", finalImgURL);
                intent.putExtra("recipeName", recipeName);

                startActivity(intent);

            }
        });
        String imgURL = myRecipe.getRecipePic();
        String foodName = myRecipe.getName();
        Picasso.with(getApplicationContext()).load(imgURL).into(recipeIMG);
        recipeIMG.setScaleType(ImageView.ScaleType.FIT_XY);
        recipeIMG.setPadding(0,0,0,0);
        String boldedFoodName = "<b>" + foodName + "</b>";
        recipeName.setText(Html.fromHtml(boldedFoodName));
        recipeName.setTextColor(Color.BLACK);
        layout.setPadding(0,0,0,0);
        layout.setGravity(Gravity.CENTER);

        recipeIMG.setScaleType(ImageView.ScaleType.FIT_XY);
        recipeIMG.setBackground(getDrawable(R.drawable.recipe_scroll_viewer));


        pictureTextCombo.addView(recipeIMG);
        pictureTextCombo.addView(recipeName);
        layout.addView(pictureTextCombo);
        return layout;
    }
}
