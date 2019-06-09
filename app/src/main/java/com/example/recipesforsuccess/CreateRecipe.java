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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CreateRecipe extends MainPage{

    LinearLayout mainDisplay;
    private EditText createName;
    private EditText createTime;
    private ListView ingredientList;
    private ImageView image;
    private Button choosePicture;
    private Button submitIngredient;
    private Button createRecipe;
    private EditText ingredientEditor;
    private ListView stepList;
    private EditText createSteps;
    private Button submitSteps;
    private ListView equipmentList;
    private EditText createEquipment;
    private Button submitEquipment;
    private FirebaseAuth currAuth = this.passAuth();
    private FirebaseUser user = currAuth.getCurrentUser();
    String userID = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference currentUser = db.collection("USERS").document(userID);
    ArrayList<String> ingredients = new ArrayList<String>();
    ArrayList<String> steps = new ArrayList<String>();
    ArrayList<String> equipment = new ArrayList<String>();
    private StorageReference storageRef;
    private DatabaseReference dataRef;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayAdapter<String> ingredientAdapter;
    private ArrayAdapter<String> stepAdapter;
    private ArrayAdapter<String> equipmentAdapter;
    private String recipeID;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View createRecipeView = getLayoutInflater().inflate(R.layout.activity_create_recipe, null);
        mainDisplay.addView(createRecipeView);

        createName = (EditText)findViewById(R.id.createName);
        createTime = (EditText)findViewById(R.id.createTime);
        ingredientList = (ListView)findViewById(R.id.list);
        image = (ImageView)findViewById(R.id.imageView);
        choosePicture = (Button)findViewById(R.id.choosePicture);
        submitIngredient = (Button)findViewById(R.id.submit);
        createRecipe = (Button)findViewById(R.id.createRecipe);
        ingredientEditor = (EditText)findViewById(R.id.ingredientEditor);
        stepList = (ListView)findViewById(R.id.stepList);
        createSteps = (EditText)findViewById(R.id.createSteps);
        submitSteps = (Button)findViewById(R.id.submitStep);
        equipmentList = (ListView)findViewById(R.id.equipmentList);
        createEquipment = (EditText)findViewById(R.id.createEquipment);
        submitEquipment = (Button)findViewById(R.id.submitEquipment);

        context = this;

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        dataRef = FirebaseDatabase.getInstance().getReference("uploads");
        ingredientAdapter = new ArrayAdapter<String>(this, R.layout.activity_step, ingredients);
        ingredientList.setAdapter(ingredientAdapter);

        submitIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentIngredient = ingredientEditor.getText().toString();
                ingredients.add(currentIngredient);
                ingredientAdapter.notifyDataSetChanged();
            }
        });

        stepAdapter = new ArrayAdapter<String>(this, R.layout.activity_step, steps);
        stepList.setAdapter(stepAdapter);
        submitSteps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String currentStep = createSteps.getText().toString();
                steps.add(currentStep);
                stepAdapter.notifyDataSetChanged();

            }

        });

        equipmentAdapter = new ArrayAdapter<String>(this, R.layout.activity_step, equipment);
        submitEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEquipment = createEquipment.getText().toString();
                equipment.add(currentEquipment);
                equipmentAdapter.notifyDataSetChanged();

            }
        });


        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        createRecipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                System.out.println("CALLING UPLOAD FILE");
                //uploadFile();
                new uploadFile().execute();
                startActivity(new Intent(CreateRecipe.this, Recipes.class));
            }

        });


    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null)
        {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(image);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    class uploadFile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String recipe_name = createName.getText().toString();
            final String prep_time = createTime.getText().toString();
            if(imageUri != null)
            {
                System.out.println("STUCK IN UPLOAD FILE");
                StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CreateRecipe.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        //Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                System.out.println("URL IS: " + url);
                                Recipe created = new Recipe(ingredients, recipe_name, prep_time, steps, url, equipment);
                                System.out.println("CREATIED NEW RECIPE");
                                db.collection("RECIPES").add(created).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        recipeID = documentReference.getId();
                                        db.collection("USERS").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            int count = 0;
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                if(documentSnapshot.exists() && count == 0)
                                                {
                                                    ArrayList<String> IDs = (ArrayList<String>) documentSnapshot.get("personalRecipes");
                                                    IDs.add(recipeID);
                                                    db.collection("USERS").document(userID).update("personalRecipes", IDs);
                                                    count++;
                                                }else{
                                                    System.out.println("count not 0");
                                                }
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("CreateRecipe.class", "Error adding document", e);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CreateRecipe.class", "It didn't work!");
                            }
                        });


                        //  String uploadID = dataRef.push().getKey();
                        // dataRef.child(uploadID).setValue(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CreateRecipe.class", "Upload Failed");
                            }
                        });
            }
            else {
                Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
    /*
    private void uploadFile() {
        final String recipe_name = createName.getText().toString();
        final String prep_time = createTime.getText().toString();
        if(imageUri != null)
        {
            System.out.println("STUCK IN UPLOAD FILE");
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateRecipe.this, "Upload Successful", Toast.LENGTH_LONG).show();
                    //Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            System.out.println("URL IS: " + url);
                            Recipe created = new Recipe(ingredients, recipe_name, prep_time, steps, url, equipment);
                            System.out.println("CREATIED NEW RECIPE");
                            db.collection("RECIPES").add(created).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    recipeID = documentReference.getId();
                                    db.collection("USERS").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        int count = 0;
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            if(documentSnapshot.exists() && count == 0)
                                            {
                                                ArrayList<String> IDs = (ArrayList<String>) documentSnapshot.get("personalRecipes");
                                                IDs.add(recipeID);
                                                db.collection("USERS").document(userID).update("personalRecipes", IDs);
                                                count++;
                                            }else{
                                                System.out.println("count not 0");
                                            }
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("CreateRecipe.class", "Error adding document", e);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("CreateRecipe.class", "It didn't work!");
                        }
                    });


                  //  String uploadID = dataRef.push().getKey();
                   // dataRef.child(uploadID).setValue(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("CreateRecipe.class", "Upload Failed");
                }
            });
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    */
}
