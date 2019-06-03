package com.example.recipesforsuccess;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    Context context;


    public RecipeAdapter(Context context, List<Recipe> object)
    {
        super(context, 0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.food_list_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.food_name);
        TextView time = (TextView) convertView.findViewById(R.id.food_date);
        ImageView image = (ImageView) convertView.findViewById(R.id.food_image);

        Recipe recipe = getItem(position);

        name.setText(recipe.getName());
        time.setText(recipe.getPrepTime());
        try {
            URL url = new URL(recipe.getRecipePic());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            image.setImageBitmap(bmp);
        }
        catch(MalformedURLException e){
            Log.d("PersonalRecipes.class", "Didn't work");
        }
        catch(IOException e)
        {
            Log.d("PersonalRecipes.class", "exception");
        }
        //image.
        //Picasso.with(context).load(recipe.getRecipePic()).into(image);




        return convertView;
    }

}
