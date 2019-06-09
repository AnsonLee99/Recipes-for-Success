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

import com.example.recipesforsuccess.dataobjects.ShoppingListViewAdapter;
import com.example.recipesforsuccess.dataobjects.ShoppingListViewItem;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShoppingList extends MainPage {
    LinearLayout mainDisplay;
    private AutoCompleteTextView bar;
    private ArrayAdapter<String> options;
    private JSONArray res;
    private FirebaseAuth auth = this.passAuth();
    private String ID =  auth.getUid();

    private ArrayList<ShoppingListViewItem> shoppingContents;
    ShoppingListViewAdapter shoppingAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View shoppingView = getLayoutInflater().inflate(R.layout.activity_shopping_list, null);
        mainDisplay.addView(shoppingView);

        shoppingContents = new ArrayList<ShoppingListViewItem>();
        shoppingAdapter = new ShoppingListViewAdapter(shoppingContents, getApplicationContext(), false,
                new shoppingDeleter());

        // For displaying the currently selected tab
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);


        // Auto-complete searchbar
        bar = (AutoCompleteTextView) findViewById(R.id.shopping_searchBar);
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

        // Use fetchFoodList to retrieve the items in the shopping list
        // and display them on the screen as a listView
        fetchShoppingList();

        // Add To shopping Button
        Button add_to_shopping = (Button)findViewById(R.id.add_to_shopping_list);
        add_to_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToshopping(new ShoppingListViewItem(bar.getText().toString()), v);

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

    protected void removeFromshopping(int index, View v) {
        if(shoppingContents.size() > index) {
            Snackbar.make(v, "Removed: " + shoppingContents.get(index).getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
            shoppingContents.remove(index);
            shoppingAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(v, "shopping is empty!", Snackbar.LENGTH_LONG).setAction("No action", null).show();
        }
    }


    public void removeFromshopping(ShoppingListViewItem item) {
        shoppingContents.remove(item);
        shoppingAdapter.notifyDataSetChanged();
        deleteFromFirebase(item.getName());
    }

    protected void addToshopping(ShoppingListViewItem item, View v) {
        Snackbar.make(v, "Adding: " + item.getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
        shoppingContents.add(item);
        shoppingAdapter = new ShoppingListViewAdapter(shoppingContents, getApplicationContext(), false, new shoppingDeleter());

        // Update this activity's list-view to match items
        ListView listView = (ListView) findViewById(R.id.shopping_list_view);
        listView.setAdapter(shoppingAdapter);
    }

    protected void fetchShoppingList() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db.collection("USERS").document(ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                ArrayList<String> items = (ArrayList<String>) document.get("shoppingList");
                ArrayList<ShoppingListViewItem> shoppingList = new ArrayList<ShoppingListViewItem>();
                for (String item : items) {
                    // Remove the user ID from the string
                    item = item.substring(0, item.indexOf("_"));
                    // Capitalize the first letter
                    if (item.length() < 2) continue;
                    item  = item.substring(0, 1).toUpperCase() + item.substring(1);

                    // Add string to the foodList
                    shoppingList.add(new ShoppingListViewItem(item));
                }

                // Update parent container's shopping list
                ShoppingList.this.shoppingContents = (ArrayList)shoppingList.clone();

                // Update this activity's list-view to match items
                ListView listView = (ListView) findViewById(R.id.shopping_list_view);
                shoppingAdapter = new ShoppingListViewAdapter(shoppingList, getApplicationContext(), false, new ShoppingDeleter());
                listView.setAdapter(shoppingAdapter);
            }
        });

    }

    protected void pushToFirebase(HashMap<String, Object> ingredient) {
        final String docName = ingredient.get("name") + "_" + ID;
        db.collection("INGREDIENTS").document(docName).set(ingredient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("shoppingList",
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
                        db.collection("USERS").document(ID).update("shoppingList",
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

    public class ShoppingDeleter implements Callable<Void> {
        ShoppingListViewItem item;

        @Override
        public Void call() throws Exception {
            Log.d("delete", "delete called in shoppingDeleter");
            removeFromshopping(item);
            return null;
        }

        public void setItem(ShoppingListViewItem item) {
            this.item = item;
        }
    }

}