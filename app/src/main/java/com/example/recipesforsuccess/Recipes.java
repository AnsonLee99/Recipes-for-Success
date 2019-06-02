package com.example.recipesforsuccess;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.app.SearchManager;
import android.widget.SearchView;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.json.*;

import javax.annotation.Nullable;


public class Recipes extends MainPage {
    LinearLayout mainDisplay;
    String EDAMAM_API_ID = "&app_id=4934de74";
    String EDAMAM_API_KEY ="&app_key=836bfa298e5ae162b917c2b0010b9190";
    String EDAMAM_API_URL = "https://api.edamam.com/search?q=";
    String USER_QUERY = "";
    String SPOONACULAR_API_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=";
    String dataParsed = "";
    String SPOONACULAR_IMAGE_URI = "https://spoonacular.com/recipeImages/";

    int SECOND_ACTIVITY_REQUEST_CODE = 0;

    ArrayList<String> intolerances = new ArrayList();
    ArrayList<String> diets = new ArrayList();

    ArrayList<String> categories = new ArrayList();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> userBasket;

    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_recipes, null);
        mainDisplay.addView(groceryView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        //RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        //RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));

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
                openActivity2();
                Intent intent = getIntent();
                System.out.println("HERE IS THE EGGS: " + intent.getStringExtra("egg"));
            }
        });

        Button getBasket = findViewById(R.id.getBasket);
        getBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView userInput = (AutoCompleteTextView) findViewById(R.id.user);
                String user = userInput.getText().toString();
                System.out.println("user is: " + user);
                DocumentReference userDoc = db.document(("USERS/" + user));
                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userBasket = (List<String>) documentSnapshot.get("basket");
                        for(int i = 0; i < userBasket.size(); i++){
                            System.out.println("item in basket is: " + userBasket.get(i));
                        }
                        new GetRecipeByIngredients().execute();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed to get items");
                    }
                });

            }
        });

        DocumentReference userDoc = db.document(("USERS/TestUser"));
        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if( documentSnapshot.exists() ){
                    userBasket = (List<String>) documentSnapshot.get("basket");
                    for(int i = 0; i < userBasket.size(); i++){
                        System.out.println("item in basket is: " + userBasket.get(i));
                    }
                    new GetRecipeByIngredients().execute();
                }
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

            }
        }
    }

    public void openActivity2(){
        Intent intent = new Intent(this, Filters.class);
        startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.search_bar:
                TextView txt = (TextView) findViewById(R.id.textView2);
                txt.setText("hi searb-bar");
                return true;
        }
        return true;
    }*/

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
    // TODO

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
                    finalIng = userBasket.get(ingInd);
                }
                System.out.println("final ingredient is: " + finalIng);
                //System.out.println("basket item as array: " + Arrays.toString(ingArray));
                if(ingInd == userBasket.size() - 1) {
                    ingredients += finalIng;
                }else{
                    ingredients += (finalIng + "%2C+");
                }

            }
            String apiURL = base_url + numSearch + maxUsed + ingredients;
            System.out.println("apiURL is: " + apiURL);
            try {
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");
                System.out.println("connection code: " + urlConnection.getResponseCode());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    //System.out.println("getInputStream is: " + bufferedReader.readLine());
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    //System.out.println("returning reponse of: " + stringBuilder.toString());
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                System.out.println("caught exception " + e);
                return null;
            }
        }

        protected void onPostExecute(String response){
            try{
                //System.out.println("the response is: " + response);
                System.out.println("IN POST EXECUTE");
                /*TextView txt = (TextView) findViewById(R.id.recipe_text);
                txt.setText(response);*/
                // Remove all current photos on new search
                LinearLayout photos = (LinearLayout) findViewById(R.id.recommended_recipes);
                if( photos.getChildCount() > 0){
                    photos.removeAllViews();
                }
                int numToRetrieve = 5;
                System.out.println("GETTING JSONOBJECT");
                //JSONObject jsonObject = new JSONObject(response);
                //System.out.println("jsonObject is: "+ jsonObject);
                JSONArray jsonArray = new JSONArray(response);
                System.out.println("json array is: " + jsonArray);
                for(int xx = 0; xx < numToRetrieve; xx++) {
                    System.out.println("retrieiving shit num: " + xx);
                    dataParsed = "";
                    JSONObject hit = jsonArray.getJSONObject(xx);
                    id = hit.getInt("id");

                    //String recipeInformation = new RecipeInstructions().execute().get();
                    //JSONObject recipe = new JSONObject(recipeInformation);
                    //String prepTime = recipe.getString("readyInMinutes");
                    String prepTime = "";
                    String imgURL = hit.getString("image");
                    String foodName = hit.getString("title");
                    photos.addView(insertIMG(imgURL, foodName, prepTime, hit, 1, ""/*recipeInformation*/));
                }

            }catch(JSONException e){
                System.out.println("caught exception " + e);
            /*} catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();*/
            }
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
                String test2 = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=pie&intolerances=egg%2Ctree+nut&diet=vegetarian";
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");

                System.out.println("URL STRING AFTER FILTERS: " + url.toString());

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
            System.out.println("urlSTRING" + urlString);
            return urlString;
        }

        // Get result from API and return result back to main UI thread
        protected void onPostExecute(String response){
            if(response == null){
                response = "There was an error";
            }

            try{
                /*System.out.println("the response is: " + response);
                TextView txt = (TextView) findViewById(R.id.recipe_text);
                txt.setText(response);*/
                // Remove all current photos on new search
                LinearLayout photos = (LinearLayout) findViewById(R.id.popular_recipes);
                if( photos.getChildCount() > 0){
                    photos.removeAllViews();
                }
                int numToRetrieve = 5;
                JSONObject jsonObject = new JSONObject(response);

                //JSONArray jsonArray = jsonObject.getJSONArray("hits"); // hits is for Edamam
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for(int xx = 0; xx < numToRetrieve; xx++) {
                        dataParsed = "";
                        JSONObject hit = jsonArray.getJSONObject(xx);
                        String imgURL = SPOONACULAR_IMAGE_URI + hit.getString("image");
                        String foodName = hit.getString("title");
                        String prepTime = hit.getString("readyInMinutes");

                        //parsing ingredients



                        //System.out.println("data parsed is: " + dataParsed);
                       // System.out.println("img url is: " + imgURL);
                        photos.addView(insertIMG(imgURL, foodName, prepTime, hit, 0, ""));
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
                System.out.println("url is : " + url_str);
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

    View insertIMG(final String imgURL, final String foodName, final String prepTime, final JSONObject hit,
                   final int task, final String instructions){

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(1000, 500));
        ImageButton recipeIMG = new ImageButton(getApplicationContext());
        recipeIMG.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        TextView recipeName = new TextView(getApplicationContext());
        recipeName.setLayoutParams(new LinearLayout.LayoutParams(700, 300));
        recipeName.setPadding(75,50,0,0);

        recipeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                String finalImgURL = "";
                String recipeName = "";
                String ingredientList = "";

                try {
                    //JSONObject recipe = hit1.getJSONObject("results");
                    recipeName = hit.getString("title");


                    id = hit.getInt("id");
                    System.out.println("ID FOR RECIPE" + id);

                    finalImgURL = imgURL;

                    String recipeInfo = "";
                    if( task == 0 ) {
                        recipeInfo = new RecipeInstructions().execute().get();
                    }else{
                        //recipeInfo = instructions; //with prep time
                        recipeInfo = new RecipeInstructions().execute().get(); // without preptime
                    }


                    JSONObject recipeSearch = new JSONObject(recipeInfo);
                    JSONArray analyzedInstructions = recipeSearch.getJSONArray("analyzedInstructions");
                    JSONObject instructionsArray = analyzedInstructions.getJSONObject(0);
                    JSONArray stepsArray = instructionsArray.getJSONArray("steps");


                    //getting ingredients list
                    JSONArray extendedIngredients = recipeSearch.getJSONArray("extendedIngredients");

                    for (int ii = 0; ii < extendedIngredients.length(); ii++){
                        JSONObject ingredient = extendedIngredients.getJSONObject(ii);
                        String original = ingredient.getString("original");
                        ingredientList = ingredientList + (ii + 1) + ".   " + "" + original + "\n" + "" + "\n";
                    };

                    for(int ii = 0; ii < stepsArray.length(); ii++){
                        JSONObject step = stepsArray.getJSONObject(ii);
                        int number = step.getInt("number");
                        String stepInfo = step.getString("step");
                        dataParsed = dataParsed + number + ".   " + "" + stepInfo + "\n" + "" +
                                "\n";
                    }

                    System.out.println("HERE IS INSTRUCTIONS_CALL");
                    System.out.println("INSTRUCTIONS HERE " + recipeInfo);

                    intent.putExtra("ingredients", ingredientList);
                    intent.putExtra("instructions", dataParsed);
                    intent.putExtra("imgURL", finalImgURL);
                    intent.putExtra("recipeName", recipeName);
                    System.out.println("HELLO ABOUT TO START");
                    startActivity(intent);
                    System.out.println("STARTED");


                } catch (Exception e) {

                    System.out.println("exception is: " + e);
                    //intent.putExtra("instructions", dataParsed);
                    intent.putExtra("imgURL", imgURL);
                    intent.putExtra("recipeName", recipeName);
                    startActivity(intent);
                    System.out.println("AFTER STARTING NEW INTENT ACTIVITY");
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
                try {
                    //JSONObject recipe = hit1.getJSONObject("results");
                    recipeName = hit.getString("title");

                    //int servings = hit.getInt("servings");
                    //dataParsed = "This recipe yields " + servings + " servings\n";

                    id = hit.getInt("id");
                    System.out.println("ID FOR RECIPE" + id);
                    finalImgURL = imgURL;
                    String recipeInfo = "";
                    if( task == 0 ) {
                        recipeInfo = new RecipeInstructions().execute().get();
                    }else{
                        recipeInfo = new RecipeInstructions().execute().get();
                    }

                    JSONObject recipeSearch = new JSONObject(recipeInfo);
                    JSONArray analyzedInstructions = recipeSearch.getJSONArray("analyzedInstructions");
                    JSONObject instructionsArray = analyzedInstructions.getJSONObject(0);
                    JSONArray stepsArray = instructionsArray.getJSONArray("steps");

                    //getting ingredients list
                    JSONArray extendedIngredients = recipeSearch.getJSONArray("extendedIngredients");
                    System.out.println("ingredients is: " + extendedIngredients.toString());
                    for (int ii = 0; ii < extendedIngredients.length(); ii++){
                        JSONObject ingredient = extendedIngredients.getJSONObject(ii);
                        String original = ingredient.getString("original");
                        ingredientList = ingredientList + (ii + 1) + ".   " + "" + original + "\n" + "" + "\n";
                    };

                    for(int ii = 0; ii < stepsArray.length(); ii++){
                        JSONObject step = stepsArray.getJSONObject(ii);
                        int number = step.getInt("number");
                        String stepInfo = step.getString("step");
                        dataParsed = dataParsed + number + ".   " +
                                "" + stepInfo + "\n" + "\n";
                    }

                    System.out.println("HERE IS INSTRUCTIONS_CALL");
                    System.out.println("INSTRUCTIONS HERE " + recipeInfo);

                    intent.putExtra("instructions", dataParsed);
                    intent.putExtra("ingredients", ingredientList);
                    intent.putExtra("imgURL", finalImgURL);
                    intent.putExtra("recipeName", recipeName);
                    System.out.println("HELLO ABOUT TO START");
                    startActivity(intent);
                    System.out.println("STARTED");


                } catch (Exception e) {

                    System.out.println("exception is: " + e);
                    //intent.putExtra("instructions", dataParsed);
                    intent.putExtra("imgURL", imgURL);
                    intent.putExtra("recipeName", recipeName);
                    startActivity(intent);
                    System.out.println("AFTER STARTING NEW INTENT ACTIVITY");
                }
            }
        });

        Picasso.with(getApplicationContext()).load(imgURL).into(recipeIMG);
        recipeIMG.setScaleType(ImageView.ScaleType.FIT_XY);
        recipeIMG.setPadding(0,0,0,0);
        String boldedFoodName = "<b>" + foodName + "</b>";
        recipeName.setText(Html.fromHtml(boldedFoodName));
        recipeName.append("\nPrep Time: " + prepTime + " min");
        recipeName.setTextColor(Color.BLACK);
        layout.setPadding(0,100,0,0);
        layout.addView(recipeIMG);
        layout.addView(recipeName);
        return layout;
    }
}
