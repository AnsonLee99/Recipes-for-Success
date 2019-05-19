package com.example.recipesforsuccess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends MainPage {
    LinearLayout mainDisplay;
    FirebaseAuth auth = this.passAuth();
    EditText newFirst;
    EditText newLast;
    EditText newEmail;
    EditText newPass;
    EditText confirmPass;
    EditText oldPass;
    Button logout;
    Button save;
    private FirebaseAuth currAuth = this.passAuth();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDisplay = (LinearLayout) findViewById(R.id.main_display);
        View groceryView = getLayoutInflater().inflate(R.layout.activity_profile, null);
        mainDisplay.addView(groceryView);

        // For displaying the currently selected tab
        // I can't fuckin figure it out
        RadioGroup rg = (RadioGroup) findViewById(R.id.NavBar_Group);
        RadioButton curr = (RadioButton)findViewById(R.id.recipes_tab_button);
        //curr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.<CLICKED VERSION OF ICON>);
        //curr.setTextColor(Color.parseColor("3F51B5"));

        //Firebase Stuff starts here

        newFirst = (EditText) findViewById(R.id.ChangeFirst);
        newLast = (EditText) findViewById(R.id.ChangeLast);
        newEmail = (EditText) findViewById(R.id.newEmail);
        newPass = (EditText) findViewById(R.id.newPass);
        confirmPass = (EditText) findViewById(R.id.ConfirmNewPassword);
        oldPass = (EditText) findViewById(R.id.OldPassword);
        logout = (Button) findViewById(R.id.Logout);
        save = (Button) findViewById(R.id.Save);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void signOut()
    {
        currAuth.signOut();
        startActivity(new Intent(Profile.this, MainActivity.class));
    }
    // TODO
}
