package com.example.recipesforsuccess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.SearchManager;
import android.widget.SearchView;
import android.content.Context;
import android.widget.TextView;
import android.provider.SearchRecentSuggestions;


public class Recipes extends MainPage {
    LinearLayout mainDisplay;

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
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView txt = (TextView) findViewById(R.id.recipe_text);
            txt.setText("Your query is: " + query);
        }

    }
    // TODO
}
