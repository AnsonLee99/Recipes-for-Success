package com.example.recipesforsuccess;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

public class PersonalRecipes extends MainPage {

    LinearLayout mainDisplay;
    private FirebaseAuth currAuth = this.passAuth();
    private FirebaseUser user = currAuth.getCurrentUser();
    private String userID = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeRef = db.collection("RECIPES");
    private DocumentReference currentUser = db.collection("USERS").document(userID);
    ListView list;
    private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
    RecipeAdapter adapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View createRecipeView = getLayoutInflater().inflate(R.layout.activity_tester, null);
        mainDisplay.addView(createRecipeView);
        adapter = new RecipeAdapter(context, recipes);
        list = (ListView) findViewById(R.id.test_list);
        list.setAdapter(adapter);

        currentUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    ArrayList<String> recipeIDs = (ArrayList<String>) documentSnapshot.get("personalRecipes");
                    for(String ID : recipeIDs)
                    {
                        recipeRef.document(ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                if(documentSnapshot.exists())
                                {
                                    Recipe currRecipe = documentSnapshot.toObject(Recipe.class);
                                    recipes.add(currRecipe);
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        });
                    }

                }
            }
        });
    }
}
