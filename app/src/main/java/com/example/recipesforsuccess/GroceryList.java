package com.example.recipesforsuccess;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GroceryList extends MainPage {
    LinearLayout mainDisplay;
    private AutoCompleteTextView bar;
    private ArrayAdapter<String> options;
    private JSONArray res;
    private FirebaseAuth auth = this.passAuth();
    private String ID =  auth.getUid();

    private ArrayList<FoodListViewItem> groceryContents;
    FoodListViewAdapter groceryAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_grocery_list, null);
        mainDisplay.addView(groceryView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));


        // Auto-complete searchbar
        bar = (AutoCompleteTextView) findViewById(R.id.grocery_searchBar);
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

        ListView listView = (ListView) findViewById(R.id.shopping_list_view);

        groceryContents = new ArrayList<FoodListViewItem>();
        groceryContents.add(new FoodListViewItem("Marshmallow", "April 20", R.drawable.ic_launcher_background));
        groceryContents.add(new FoodListViewItem("Pizza", "April 20", R.drawable.ic_launcher_background));
        groceryContents.add(new FoodListViewItem("Korean BBQ", "April 20", R.drawable.ic_launcher_background));
        groceryAdapter = new FoodListViewAdapter(groceryContents, getApplicationContext(), false);

        listView.setAdapter(groceryAdapter);

        // Add To Grocery Button
        Button add_to_grocery = (Button)findViewById(R.id.add_to_shopping_list);
        add_to_grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToGrocery(new FoodListViewItem(bar.getText().toString(), "Time", R.drawable.ic_launcher_background), v);

                // REPLACE "new JSONObject()" with the JSON object from the selected "res" array
                HashMap<String, Object> newIngredient = new HashMap<>();

                newIngredient.put("flag", false);
                newIngredient.put("name", bar.getText().toString());
                newIngredient.put("time added", Calendar.getInstance().getTime());

                Log.d("test", "value before: " + newIngredient.get("name"));
                pushToFirebase(newIngredient);
            }
        });
        
    }

    protected void removeFromGrocery(int index, View v) {
        if(groceryContents.size() > index) {
            Snackbar.make(v, "Removed: " + groceryContents.get(index).getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
            groceryContents.remove(index);
            groceryAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(v, "Grocery is empty!", Snackbar.LENGTH_LONG).setAction("No action", null).show();
        }
    }

    protected void removeFromGrocery(FoodListViewItem item) {
        groceryContents.remove(item);
        groceryAdapter.notifyDataSetChanged();
    }

    protected void addToGrocery(FoodListViewItem item, View v) {
        Snackbar.make(v, "Adding: " + item.getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
        groceryContents.add(item);
        groceryAdapter.notifyDataSetChanged();
    }

    protected void pushToFirebase(HashMap<String, Object> ingredient) {
        final String docName = ingredient.get("name") + "_" + ID;
        db.collection("INGREDIENTS").document(docName).set(ingredient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("Shopping List",
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

    // itemToDelete is the name of the item to delete
    protected void deleteFromFirebase(String itemToDelete) {

        final String docName = itemToDelete + "_" + ID;
        db.collection("INGREDIENTS").document(docName).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("Shopping List",
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

}
