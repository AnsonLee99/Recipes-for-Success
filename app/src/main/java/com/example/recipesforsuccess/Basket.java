package com.example.recipesforsuccess;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import org.json.JSONObject;

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

    private ArrayList<String> imgURL = new ArrayList<>();
    private ArrayList<String> prevImgURL;
    private String currImgURL;

    private ArrayList<FoodListViewItem> basketContents;
    FoodListViewAdapter basketAdapter;

    private PopupWindow window;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check if a current user is logged in
        if (auth.getCurrentUser() == null) {
            Intent in = new Intent(Basket.this, MainActivity.class);
            startActivity(in);
            return;
        }

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View basketView = getLayoutInflater().inflate(R.layout.activity_basket, null);
        mainDisplay.addView(basketView);

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

                prevImgURL = imgURL;
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

                options.addAll(currOptions);
            }

            @Override
            public void afterTextChanged(Editable s) {
                options.notifyDataSetChanged();
            }
        });

        bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currImgURL = prevImgURL.get((int)id);
            }
        });

        fetchFoodList();
        Log.d("test", "EXECUTED");

        // Add To Basket Button
        Button add_to_basket = (Button)findViewById(R.id.add_to_basket);
        add_to_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REPLACE "new JSONObject()" with the JSON object from the selected "res" array
                HashMap<String, Object> newIngredient = new HashMap<>();

                newIngredient.put("flag", false);
                newIngredient.put("name", bar.getText().toString());
                newIngredient.put("time added", Calendar.getInstance().getTime());
                newIngredient.put("imgURL", currImgURL);

                pushToFirebase(newIngredient);

                Log.d("test", "value before: " + newIngredient.get("name"));

                // capitalize first letter of item name
                String ingredientName = newIngredient.get("name").toString();
                ingredientName = (ingredientName.length() < 2) ? ingredientName :
                        (ingredientName.substring(0, 1).toUpperCase() + ingredientName .substring(1));

                // capitalize first letter of item name
                String itemname = (bar.getText().toString().length() < 2) ? bar.getText().toString() :
                        (bar.getText().toString().substring(0, 1).toUpperCase() + bar.getText().toString().substring(1));
                Date date = new Date();

                addToBasket(new FoodListViewItem(itemname, dateToString(date), currImgURL), v);
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

    public void addToBasket(FoodListViewItem item, View v) {
        Snackbar.make(v, "Adding: " + item.getName(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
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

                // parse returned basket to get food item names and dates added
                basketContents = new ArrayList<>();
                // Update this activity's list-view to match items
                ListView listView = (ListView) findViewById(R.id.basket_list_view);
                basketAdapter = new FoodListViewAdapter(basketContents, getApplicationContext(), false,
                       new PopulatePopup(), new BasketDeleter()
                 );

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
                            String img = foodItemDocument.get("imgURL").toString();

                            basketContents.add(new FoodListViewItem(itemname, dateToString(date), img));
                            basketAdapter.notifyDataSetChanged();

                        }
                    });
                }

                basketAdapter.notifyDataSetChanged();
            }
        });
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

    // Shows popup with nutritional info
    private void showPopup(String ingredient) {
        try {
            LayoutInflater inflater = (LayoutInflater) Basket.this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.nutritioanl_info_popup, (ViewGroup) findViewById(R.id.nutritional_popup) );

            window = new PopupWindow(layout, 500, 700, true);

            window.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button close = (Button) layout.findViewById(R.id.popup_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                }
            });

            TextView title = (TextView) layout.findViewById(R.id.nutritional_info_title);
            title.setText(ingredient);

            PopulatePopup updatePopup = new PopulatePopup(ingredient);
            updatePopup.execute("https://api.nal.usda.gov/ndb/list");
            //updatePopup.execute("Browser: https://api.nal.usda.gov/ndb/search/?format=json&q=butter&sort=n&max=25&offset=0&api_key=DEMO_KEY ");
            
        } catch(Exception e) {
            Log.d("TEST", "ERROR OCCURED WITH POPUP");
            e.printStackTrace();
        }
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


    public class PopulatePopup extends AsyncTask<String, Void, JSONObject> {
        private String ingredientName;

        public PopulatePopup() {};

        public PopulatePopup(String ingredientName) {
            this.ingredientName = ingredientName;
        }

        public void setItemName(String itemname){
            ingredientName = itemname;
        }

        public void call() {
            // do whatever here
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result = new JSONObject();

            try {
                StringBuilder url = new StringBuilder(urls[0]);
                url.append("?api_key=LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ&format=json&sort=r&max=1&da=Standard Referece&q=" + ingredientName);



/*

            String rawURL = convertToREST(params, urls[0]);
            URL url = new URL(rawURL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
//            con.setRequestProperty("format", "json");
//            con.setRequestProperty("q", ingredientName);
//            con.setRequestProperty("sort", "r");
            //con.("api_key", "LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ");
//            con.setRequestProperty("max", "1");
//            con.setRequestProperty("ds", "Standard Reference");

            Log.d("test", "URL = " + url.toString());

            HttpURLConnection.setFollowRedirects(true);
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

            // Print the response code
            // and response message from server.
            Log.d("test", "Response Code:"
                    + con.getResponseCode());
            Log.d("test", "Response Message:"
                    + con.getResponseMessage());


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("api_key", "LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("test", "CONTENTS: " + line);
            }
*/
            } catch (Exception e) {
                Log.d("test", "ERROR WITH QUERYING FOR NDBNO ID: " + e );
            }




            return null;
        }

    }

}