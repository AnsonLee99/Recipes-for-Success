package com.example.recipesforsuccess;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class Profile extends MainPage {
    LinearLayout mainDisplay;
    //FirebaseAuth auth = this.passAuth();
    EditText newFirst;
    EditText newLast;
    EditText newEmail;
    EditText newPass;
    EditText confirmPass;
    EditText oldPass;
    Button logout;
    Button save;
    private FirebaseAuth currAuth = this.passAuth();
    private FirebaseUser user = currAuth.getCurrentUser();
    String userID = user.getUid();
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
        newEmail = (EditText) findViewById(R.id.ChangeEmail);
        newPass = (EditText) findViewById(R.id.NewPassword);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reAuthenticate();
            }
        });

    }

    public void signOut()
    {
        currAuth.signOut();
        startActivity(new Intent(Profile.this, MainActivity.class));
    }

    public void reAuthenticate()
    {

        String getOldPass = oldPass.getText().toString();

        //TODO
        //Update this to require the password in order to update.
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), getOldPass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    updateInfo();
                    Toast.makeText(Profile.this, "Update Successful", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("Profile.class", "Authentication Unsuccessful");
                }
            }
        });

    }

    public void updateInfo()
    {
        String getFirst = newFirst.getText().toString();
        if(TextUtils.isEmpty(getFirst)) {
            getFirst = "";
        }
        String getLast = newLast.getText().toString();
        if(TextUtils.isEmpty(getLast))
        {
            getLast = "";
        }

        String getEmail = newEmail.getText().toString();
        if(TextUtils.isEmpty(getEmail))
        {
            getEmail = "";
        }
        String getPass = newPass.getText().toString();
        if(TextUtils.isEmpty(getPass))
        {
            getPass = "";
        }
        String getConfirm = confirmPass.getText().toString();
        if(TextUtils.isEmpty(getConfirm))
        {
            getConfirm = "";
        }

        DocumentReference currentUser = db.collection("USERS").document(userID);

        if(getFirst.length() > 0)
        {
            currentUser.update("firstName", getFirst);
        }
        if(getLast.length() > 0)
        {
            currentUser.update("lastName", getLast);
        }


        if(getEmail.length() > 0)
        {

            user.updateEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Log.d("Profile.class", "Email Updated");
                    }
                    else {
                        Log.d("Profile.class", "Update Email Unsuccessful");
                    }
                }
            });

        }

        if(getPass.length() > 0 && getPass.equals(getConfirm))
        {

            user.updatePassword(getPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d("Profile.class", "Password Updated" );
                    }
                    else {
                        Log.d("Profile.class", "Update Password Unsuccessful");
                    }
                }
            });

        }



    }
    // TODO
}
