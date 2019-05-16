package com.example.recipesforsuccess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.app.SearchManager;
import android.widget.SearchView;
import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

import com.squareup.picasso.Picasso;

import org.json.*;

public class Recipes extends MainPage {
    LinearLayout mainDisplay;
    String EDAMAM_API_ID = "&app_id=4934de74";
    String EDAMAM_API_KEY ="&app_key=836bfa298e5ae162b917c2b0010b9190";
    String EDAMAM_API_URL = "https://api.edamam.com/search?q=";
    String USER_QUERY = "";
    String SPOONACULAR_API_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=";
    String dataParsed = "";
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
        return true;
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
        }

    }
    // TODO

    // Background thread used to retrieve data from API
    class RecipeSearch extends AsyncTask<Void, Void, String>{

        private Exception exception;

        // Step used to set up retrieving data from API
        protected void onPreExecute(){

        }

        // Retrieves API data using user query
        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(EDAMAM_API_URL + USER_QUERY + EDAMAM_API_ID + EDAMAM_API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // Using the code below with spoonacular needs threading i think
                /*URL url = new URL(SPOONACULAR_API_URL + USER_QUERY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                urlConnection.setRequestProperty("X-RapidAPI-Key", "94cfccb0c9msh2df1f90eef1052fp15b07bjsna54028f8b980");
                System.out.println("response code: " + urlConnection.getResponseCode());*/
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

        // Get result from API and return result back to main UI thread
        protected void onPostExecute(String response){
            if(response == null){
                response = "There was an error";
            }

            try{
                // Remove all current photos on new search
                LinearLayout photos = (LinearLayout) findViewById(R.id.popular_recipes);
                if( photos.getChildCount() > 0){
                    photos.removeAllViews();
                }
                int numToRetrieve = 5;
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("hits");
                    for(int xx = 0; xx < numToRetrieve; xx++) {
                        dataParsed = "";
                        JSONObject hit1 = jsonArray.getJSONObject(xx);
                        String imgURL = hit1.getJSONObject("recipe").getString("image");
                        //System.out.println("data parsed is: " + dataParsed);
                        photos.addView(insertIMG(imgURL, hit1));
                    }

            }catch(JSONException e){}
        }
        View insertIMG(final String imgURL, final JSONObject hit1){

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(350, 500));
            ImageButton recipeIMG = new ImageButton(getApplicationContext());
            recipeIMG.setLayoutParams(new LinearLayout.LayoutParams(300, 300));

            //try{
                //JSONObject recipe = hit1.getJSONObject("recipe");

                recipeIMG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewRecipeInstructions.class);
                        String imgURL = "";
                        String recipeName = "";
                        try {
                            JSONObject recipe = hit1.getJSONObject("recipe");
                            imgURL = recipe.getString("image");
                            recipeName = recipe.getString("label");

                            int servings = recipe.getInt("yield");
                            dataParsed = "This recipe yields " + servings + " servings\n";

                            JSONArray healthLabels = recipe.getJSONArray("healthLabels");
                            JSONArray dietLabels = recipe.getJSONArray("dietLabels");
                            for (int ii = 0; ii < healthLabels.length(); ii++) {
                                dataParsed += healthLabels.getString(ii) + "\n";
                            }
                            for (int jj = 0; jj < dietLabels.length(); jj++) {
                                dataParsed += dietLabels.getString(jj) + "\n";
                            }

                            JSONArray ingredients = recipe.getJSONArray("ingredientLines");
                            dataParsed += "\nIngredients:\n";
                            for (int kk = 0; kk < ingredients.length(); kk++) {
                                dataParsed += ingredients.getString(kk) + "\n";
                            }
                        }catch(JSONException e){

                        }
                        intent.putExtra("instructions", dataParsed);
                        intent.putExtra("imgURL", imgURL);
                        intent.putExtra("recipeName", recipeName);
                        startActivity(intent);
                    }
                });

            //}catch(JSONException e){}

            Picasso.with(getApplicationContext()).load(imgURL).into(recipeIMG);
            layout.addView(recipeIMG);
            return layout;
        }

    }

}
