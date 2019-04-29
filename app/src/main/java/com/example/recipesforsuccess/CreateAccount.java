package com.example.recipesforsuccess;

import com.example.recipesforsuccess.Basket;
import com.example.recipesforsuccess.GroceryList;
import com.example.recipesforsuccess.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.Toast;


import com.example.recipesforsuccess.Profile;
import com.example.recipesforsuccess.R;
import com.example.recipesforsuccess.Recipes;
import com.example.recipesforsuccess.Social;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccount extends MainActivity {

    private EditText createEmail;
    private EditText createPassword;
    private Button createButton;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener nAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createEmail = (EditText) findViewById(R.id.newEmail);
        createPassword = (EditText) findViewById(R.id.newPass);
        createButton = (Button) findViewById(R.id.create);
        nAuth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void signUp() {
        String email = createEmail.getText().toString();
        String password = createPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(CreateAccount.this, "Email is empty", Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(CreateAccount.this, "Password is empty", Toast.LENGTH_LONG).show();

            }
        } else {


            nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (!(task.isSuccessful())) {
                        Toast.makeText(CreateAccount.this, "Sign In Problem", Toast.LENGTH_LONG).show();
                    } else {
                        //user = mAuth.getCurrentUser();
                        startActivity(new Intent(CreateAccount.this, MainActivity.class));
                    }

                }
            });
        }

    }

}

