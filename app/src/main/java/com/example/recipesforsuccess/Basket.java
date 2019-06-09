package com.example.recipesforsuccess;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
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
    private Context context;

    private Button clear;

    private long delay = 300; // Wait .5 sec after user stops typing to update
    long lastTextUpdate = 0;
    Handler handler = new Handler();
    private Editable str;

    private ArrayList<String> imgURL = new ArrayList<>();

    private ArrayList<FoodListViewItem> basketContents = new ArrayList<>();
    FoodListViewAdapter basketAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        basketAdapter = new FoodListViewAdapter(basketContents, getApplicationContext(), false, new BasketDeleter());
        context = this;

        // Check if a current user is logged in
        if (auth.getCurrentUser() == null) {
            Intent in = new Intent(Basket.this, MainActivity.class);
            startActivity(in);
            return;
        }

        setSelected(2);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View basketView = getLayoutInflater().inflate(R.layout.activity_basket, null);
        mainDisplay.addView(basketView);

        clear = (Button) findViewById(R.id.basket_search_clear);

        Log.d("test", "clear button: " + clear) ;
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setText("");
                clear.setVisibility(View.GONE);
            }
        });

        // Auto-complete searchbar
        bar = (AutoCompleteTextView) findViewById(R.id.basket_searchBar);
        options = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        bar.setAdapter(options);
        options.notifyDataSetChanged();

        // Runs Autocomplete options update after 500 sec of inactivity in textview
        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (lastTextUpdate + delay - 500)) {
                    Log.d("test", "NEW ENTRY: " + str.toString());

                    ExecutorService service = Executors.newSingleThreadExecutor();
                    CustomCallable call = new CustomCallable(str.toString());
                    Future<JSONArray> f = service.submit(call);

                    options.clear();
                    ArrayList<String> currOptions = new ArrayList<>();

                    imgURL = new ArrayList<>();

                    try {
                        res = f.get();
                        for ( int i = 0; i < res.length(); i ++ ) {
                            currOptions.add(res.getJSONObject(i).get("name").toString());
                            imgURL.add(res.getJSONObject(i).get("image").toString());
                        }
                    } catch(Exception e) {
                        Log.d("test", "EXCEPTION WITH RETRIEVING ARRAYLIST: " + e);
                    }
                    service.shutdown();

                    bar.setAdapter(options);
                    options.addAll(currOptions);
                    options.notifyDataSetChanged();
                }
            }
        };

        bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                lastTextUpdate = System.currentTimeMillis();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >0) {
                    handler.postDelayed(input_finish_checker, delay);
                    str = s;
                    clear.setVisibility(View.VISIBLE);
                }
                else {
                    clear.setVisibility(View.GONE);
                }
            }
        });

        fetchFoodList();
        Log.d("test", "EXECUTED");

        // Add To Basket Button
        Button add_to_basket = (Button)findViewById(R.id.add_to_basket);
        add_to_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bar.getText().toString().length() != 0 ) {
                    int idx = 0;
                    for (int i = 0; i < options.getCount(); i++) {
                        if (options.getItem(i) == bar.getText().toString()) {
                            idx = i;
                        }
                    }

                    // REPLACE "new JSONObject()" with the JSON object from the selected "res" array
                    HashMap<String, Object> newIngredient = new HashMap<>();

                    newIngredient.put("flag", false);
                    newIngredient.put("name", bar.getText().toString());
                    newIngredient.put("time added", Calendar.getInstance().getTime());
                    newIngredient.put("imgURL", imgURL.get(idx));

                    pushToFirebase(newIngredient);

                    Log.d("test", "value before: " + newIngredient.get("name"));

                    // capitalize first letter of item name
                    String ingredientName = newIngredient.get("name").toString();
                    ingredientName = (ingredientName.length() < 2) ? ingredientName :
                            (ingredientName.substring(0, 1).toUpperCase() + ingredientName.substring(1));

                    // capitalize first letter of item name
                    String itemname = (bar.getText().toString().length() < 2) ? bar.getText().toString() :
                            (bar.getText().toString().substring(0, 1).toUpperCase() + bar.getText().toString().substring(1));
                    Date date = new Date();

                    addToBasket(new FoodListViewItem(itemname, dateToString(date), imgURL.get(idx), context, basketAdapter));
                    bar.setText("");
                    clear.setVisibility(View.GONE);
                }
            }
        });
    }

    public void removeFromBasket(int index, View v) {
        if(basketContents.size() > index) {
            Snackbar.make(v, "Removed: " + basketContents.get(index).getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
            basketContents.remove(index);
            basketAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(v, "Basket is empty!", Snackbar.LENGTH_LONG).setAction("No action", null).show();
        }
    }

    public void removeFromBasket(FoodListViewItem item) {
        basketContents.remove(item);
        basketAdapter.notifyDataSetChanged();
        deleteFromFirebase(item.getName());
    }

    public void addToBasket(FoodListViewItem item) {
        // capitalize first letter of item name
        basketContents.add(item);
        basketAdapter.notifyDataSetChanged();
    }

    protected void pushToFirebase(HashMap<String, Object> ingredient) {
        final String docName = ingredient.get("name") + "_" + ID;
        db.collection("INGREDIENTS").document(docName).set(ingredient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("basket",
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
                ArrayList<String> items = (ArrayList<String>) document.get("basket");

                // parse returned basket to get food item names and dates added                // Update this activity's list-view to match items
                ListView listView = (ListView) findViewById(R.id.basket_list_view);

                listView.setAdapter(basketAdapter);

                if(items.size() <= 0) {
                    return;
                }

                for (String item : items) {
                    // get date of food item
                    db.collection("INGREDIENTS").document(item).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot foodItemDocument = task.getResult();

                            Object itemobj = foodItemDocument.get("name");
                            String itemname;
                            if(itemobj != null) {
                                itemname = itemobj.toString();
                            } else {
                                return;
                            }

                            // capitalize first letter of item name
                            itemname = (itemname.length() < 2) ? itemname : (itemname.substring(0, 1).toUpperCase() + itemname.substring(1));

                            String itemdate = foodItemDocument.get("time added").toString();
                            String[] itemdates = itemdate.split(",");
                            String itemseconds = itemdates[0].replace("Timestamp(seconds=", "");
                            String itemnano = itemdates[1].replace(" nanoseconds=", "").replace(")", "");

                            Date date = new Date(Long.parseLong(itemseconds) * 1000l);

                            // Add string to the foodList
                            String img;
                            try {
                                img = foodItemDocument.get("imgURL").toString();
                            } catch (Exception e){
                                img = "";
                            }

                            addToBasket(new FoodListViewItem(itemname, dateToString(date), img, context, basketAdapter));
                            basketAdapter.notifyDataSetChanged();
                        }
                    });
                    basketAdapter.notifyDataSetChanged();
                }
            }
        });
        basketAdapter.notifyDataSetChanged();
    }

    private String dateToString(Date date) {

        String[] splitDate = date.toString().split(" ");
        return "Added: " + splitDate[1] + " " + splitDate[2];
    }

    // itemToDelete is the name of the item to delete
    protected void deleteFromFirebase(String itemToDelete) {

        Log.d("delete", "deleting item " + itemToDelete);

        final String docName = itemToDelete.toLowerCase() + "_" + ID;
        db.collection("INGREDIENTS").document(docName).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("USERS").document(ID).update("basket",
                                FieldValue.arrayRemove(docName));
                        Log.d("delete", "DELETING SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("delete", "DELETING FAILED");
                    }
                });
    }

    public class BasketDeleter implements Callable<Void> {
        FoodListViewItem item;

        @Override
        public Void call() throws Exception {
            Log.d("delete", "delete called in BasketDeleter");
            removeFromBasket(item);
            return null;
        }

        public void setItem(FoodListViewItem item) {
            this.item = item;
        }
    }

}