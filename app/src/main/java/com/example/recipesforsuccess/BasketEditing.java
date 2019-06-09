package com.example.recipesforsuccess;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.recipesforsuccess.dataobjects.FoodListViewAdapter;
import com.example.recipesforsuccess.dataobjects.FoodListViewItem;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BasketEditing extends MainPage {
    LinearLayout mainDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View shoppingView = getLayoutInflater().inflate(R.layout.activity_basket_editing, null);
        mainDisplay.addView(shoppingView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));

        ListView listView = (ListView) findViewById(R.id.basket_list_view);

        ArrayList<FoodListViewItem> content = new ArrayList<FoodListViewItem>();
//        content.add(new FoodListViewItem("Marshmallow", "April 20", R.drawable.ic_launcher_background));
//        content.add(new FoodListViewItem("Pizza", "April 20", R.drawable.ic_launcher_background));
//        content.add(new FoodListViewItem("Korean BBQ", "April 20", R.drawable.ic_launcher_background));
//        content.add(new FoodListViewItem("Pho", "April 20", R.drawable.ic_launcher_background));
//        content.add(new FoodListViewItem("Silkworms", "April 20", R.drawable.ic_launcher_background));
//        content.add(new FoodListViewItem("Computer Chips", "April 20", R.drawable.ic_launcher_background));
//        FoodListViewAdapter adapter = new FoodListViewAdapter(content, getApplicationContext(), true,
//                new Callable<Void>() {
//                    @Override
//                    public Void call() throws Exception {
//                        showPop()
//                        return null;
//                    }
//                });
//
//        listView.setAdapter(adapter);

    }

    // TODO
}
