package com.example.recipesforsuccess;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.recipesforsuccess.dataobjects.FoodListViewAdapter;
import com.example.recipesforsuccess.dataobjects.FoodListViewItem;

import java.util.ArrayList;

public class Basket extends MainPage {
    LinearLayout mainDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_basket, null);
        mainDisplay.addView(groceryView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));

        ListView listView = (ListView) findViewById(R.id.basket_list_view);

        ArrayList<FoodListViewItem> content = new ArrayList<FoodListViewItem>();
        content.add(new FoodListViewItem("Marshmallow", "April 20", R.drawable.ic_launcher_background));
        content.add(new FoodListViewItem("Pizza", "April 20", R.drawable.ic_launcher_background));
        content.add(new FoodListViewItem("Korean BBQ", "April 20", R.drawable.ic_launcher_background));
        content.add(new FoodListViewItem("Pho", "April 20", R.drawable.ic_launcher_background));
        content.add(new FoodListViewItem("Silkworms", "April 20", R.drawable.ic_launcher_background));
        content.add(new FoodListViewItem("Computer Chips", "April 20", R.drawable.ic_launcher_background));
        FoodListViewAdapter adapter = new FoodListViewAdapter(content, getApplicationContext());

        listView.setAdapter(adapter);

    }

    // TODO
}
