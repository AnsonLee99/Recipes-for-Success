package com.example.recipesforsuccess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.core.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "NAVBAR";
    private RadioGroup navBar;
    //Global variables for login
    private EditText emailField;
    private EditText passwordField;
    //Sign in Button
    private Button loginButton;
    private Button createAccount;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        emailField = (EditText) findViewById(R.id.Email);
        passwordField = (EditText) findViewById(R.id.Password);
        loginButton = (Button) findViewById(R.id.loginButton);
        createAccount = (Button) findViewById(R.id.createAccount);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, Basket.class));
                }
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, Basket.class));
                signIn();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccount.class));
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        user = mAuth.getCurrentUser();
/*
        if(user != null)
        {
            new Intent(MainActivity.this, Basket.class);
        }
  */

    }

    private void signIn() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Email is empty", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Password is empty", Toast.LENGTH_LONG).show();

                }
            }
        else {


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (!(task.isSuccessful())) {
                            Toast.makeText(MainActivity.this, "Sign In Problem", Toast.LENGTH_LONG).show();
                        }

                        else {
                            //user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, Basket.class));
                        }

                    }
                });
            }

        }

    }
