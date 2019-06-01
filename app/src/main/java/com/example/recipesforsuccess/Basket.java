package com.example.recipesforsuccess;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.recipesforsuccess.dataobjects.FoodListViewAdapter;
import com.example.recipesforsuccess.dataobjects.FoodListViewItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Basket extends MainPage {
    LinearLayout mainDisplay;
    private AutoCompleteTextView bar;
    private ArrayAdapter<String> options;
    private JSONArray res;
    private FirebaseAuth auth = this.passAuth();
    private String ID =  auth.getUid();

    private ArrayList<FoodListViewItem> basketContents;
    FoodListViewAdapter basketAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View basketView = getLayoutInflater().inflate(R.layout.activity_basket, null);
        mainDisplay.addView(basketView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));


        // Auto-complete searchbar
        bar = (AutoCompleteTextView) findViewById(R.id.basket_searchBar);
        options = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        bar.setAdapter(options);
        //bar.setDropDownHeight(6);
        options.notifyDataSetChanged();

        bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("test", "NEW ENTRY: " + s.toString());

                ExecutorService service = Executors.newSingleThreadExecutor();
                CustomCallable call = new CustomCallable(s.toString());
                Future<JSONArray> f = service.submit(call);

                options.clear();
                ArrayList<String> currOptions = new ArrayList<>();
                try {
                    res = f.get();
                    for ( int i = 0; i < res.length(); i ++ ) {
                        currOptions.add(res.getJSONObject(i).get("name").toString());
                    }
                } catch(Exception e) {
                    Log.d("test", "EXCEPTION WITH RETRIEVING ARRAYLIST: " + e);
                }
                service.shutdown();

                options.addAll(currOptions);
            }

            @Override
            public void afterTextChanged(Editable s) {
                options.notifyDataSetChanged();
            }
        });

        // Use fetchFoodList to get ingredients from database and add them to listview
        fetchFoodList();
        ListView listView = (ListView) findViewById(R.id.basket_list_view);
        basketContents = new ArrayList<FoodListViewItem>();
        basketAdapter = new FoodListViewAdapter(basketContents, getApplicationContext(), false);

        listView.setAdapter(basketAdapter);

        // Add To Basket Button
        Button add_to_basket = (Button)findViewById(R.id.add_to_basket);
        add_to_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToBasket(new FoodListViewItem(bar.getText().toString(), "Time", R.drawable.ic_launcher_background), v);

                // REPLACE "new JSONObject()" with the JSON object from the selected "res" array
                HashMap<String, Object> newIngredient = new HashMap<>();

                newIngredient.put("flag", false);
                newIngredient.put("name", bar.getText().toString());
                newIngredient.put("time added", Calendar.getInstance().getTime());

                Log.d("test", "value before: " + newIngredient.get("name"));
                pushToFirebase(newIngredient);
            }
        });


        // DEBUG BUTTONS TO ADD OR DELETE ITEM FROM LISTVIEW
        Button debug_add_item = (Button)findViewById(R.id.debug_add_item);
        Button debug_delete_item = (Button)findViewById(R.id.debug_delete_item);

        debug_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToBasket(new FoodListViewItem("NewFood", "Today", R.drawable.ic_launcher_background), v);
            }
        });

        debug_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromBasket(basketContents.size() - 1, v);

            }
        });

    }

    protected void removeFromBasket(int index, View v) {
        if(basketContents.size() > index) {
        Snackbar.make(v, "Removed: " + basketContents.get(index).getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
        basketContents.remove(index);
        basketAdapter.notifyDataSetChanged();
    } else {
        Snackbar.make(v, "Basket is empty!", Snackbar.LENGTH_LONG).setAction("No action", null).show();
    }
}

    protected void removeFromBasket(FoodListViewItem item) {
        basketContents.remove(item);
        basketAdapter.notifyDataSetChanged();
    }

    protected void addToBasket(FoodListViewItem item, View v) {
        Snackbar.make(v, "Adding: " + item.getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
        basketContents.add(item);
        basketAdapter.notifyDataSetChanged();
    }

    protected void pushToFirebase(HashMap<String, Object> ingredient) {
        final String docName = ingredient.get("name") + "_" + ID;
        db.collection("INGREDIENTS").document(docName).set(ingredient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("Basket",
                                FieldValue.arrayUnion(docName));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("test", "UPLOADING FAILED");
                    }
                });
    }

    protected void fetchFoodList() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db.collection("USERS").document(ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                ArrayList<String> items = (ArrayList<String>) document.get("Basket");
                ArrayList<FoodListViewItem> foodList = new ArrayList<FoodListViewItem>();
                for (String item : items) {
                    // Remove the user ID from the string
                    item = item.substring(0, item.indexOf("_"));
                    // Capitalize the first letter
                    if (item.length() < 2) continue;
                    item  = item.substring(0, 1).toUpperCase() + item.substring(1);

                    // Add string to the foodList
                    foodList.add(new FoodListViewItem(item, "4/20/1420", 0));
                }

                // Update this activity's list-view to match items
                ListView listView = (ListView) findViewById(R.id.basket_list_view);
                basketAdapter = new FoodListViewAdapter(foodList, getApplicationContext(), false);
                listView.setAdapter(basketAdapter);
            }
        });

    }

    // itemToDelete is the name of the item to delete
    protected void deleteFromFirebase(String itemToDelete) {

        final String docName = itemToDelete + "_" + ID;
        db.collection("INGREDIENTS").document(docName).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("Basket",
                                FieldValue.arrayRemove(docName));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("test", "DELETING FAILED");
                    }
                });
    }




    // TODO
}