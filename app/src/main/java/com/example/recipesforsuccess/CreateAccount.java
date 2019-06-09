package com.example.recipesforsuccess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccount extends MainActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText createEmail;
    private EditText createPassword;
    private Button createButton;
    private FirebaseAuth nAuth = this.passAuth();
    private FirebaseAuth.AuthStateListener nAuthListener;
    private FirebaseUser user;
    private String ID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firstName = (EditText) findViewById(R.id.FirstName);
        lastName = (EditText) findViewById(R.id.LastName);
        createEmail = (EditText) findViewById(R.id.newEmail);
        createPassword = (EditText) findViewById(R.id.newPass);
        createButton = (Button) findViewById(R.id.create);
        //nAuth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void signUp() {
        final String first = firstName.getText().toString();
        final String last = lastName.getText().toString();
        final String email = createEmail.getText().toString();
        final String password = createPassword.getText().toString();
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
                        Toast.makeText(CreateAccount.this, "Cannot Create User: Either user exists or password is not 6 characters", Toast.LENGTH_LONG).show();
                    } else {

                        nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!(task.isSuccessful())) {
                                    Toast.makeText(CreateAccount.this, "Sign In Problem: Account Not Successfully Created", Toast.LENGTH_LONG).show();
                                }

                                else {
                                    user = nAuth.getCurrentUser();
                                    ID = user.getUid();
                                    User information = new User(first, last);
                                    db.collection("USERS").document(ID).set(information);
                                    startActivity(new Intent(CreateAccount.this, Basket.class));
                                }

                            }
                        });

                    }

                }
            });
        }

    }

}

