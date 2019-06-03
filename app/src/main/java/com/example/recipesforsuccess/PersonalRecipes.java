package com.example.recipesforsuccess;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonalRecipes extends MainPage{

    LinearLayout mainDisplay;
    private FirebaseAuth currAuth = this.passAuth();
    private FirebaseUser user = currAuth.getCurrentUser();
    private String userID = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeRef = db.collection("RECIPES");
    private DocumentReference currentUser = db.collection("USERS").document(userID);
    ListView list;
    private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
    private RecipeAdapter adapter = new RecipeAdapter(this, recipes);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View createRecipeView = getLayoutInflater().inflate(R.layout.activity_tester, null);
        mainDisplay.addView(createRecipeView);

        currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snap = task.getResult();
                    ArrayList<String> personal = (ArrayList<String>)snap.get("personalRecipes");
                    for(String p : personal)
                    {
                        DocumentReference ref = recipeRef.document(p);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    DocumentSnapshot newSnap = task.getResult();
                                    Recipe currRecipe = newSnap.toObject(Recipe.class);
                                    recipes.add(currRecipe);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    ListView recipeListView = (ListView) findViewById(R.id.test_list);
                    recipeListView.setAdapter(adapter);
                }
            }
        });





    }



}
