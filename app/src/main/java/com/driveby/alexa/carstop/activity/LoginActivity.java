package com.driveby.alexa.carstop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.driveby.alexa.carstop.entitiy.UserEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.driveby.alexa.carstop.R;

import java.util.Locale;

/**
 * Created by mathi on 26.04.2018.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText a_email;
    private EditText a_pw;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the stored language
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String lang = sharedPreferences.getString(MainActivity.PREFS_LNG, null);

        //if sharedPreferences not set yet set the language on en
        if(lang == null){
            lang = "en";
        }

        //set the language
        Locale myLocale;
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        myLocale = new Locale(lang);
        sharedPreferences.edit().putString(MainActivity.PREFS_LNG, lang).apply();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        a_email = findViewById(R.id.tv_email_login);
        a_pw = findViewById(R.id.tv_password_login);

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        TextView noAccount = (TextView) findViewById(R.id.link_signup);
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open register activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void checkLogin(){
        //get input text
        String email = a_email.getText().toString();
        String password = a_pw.getText().toString();

        if (validateForm(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "loginUserWithEmail: success");

                        // We need an Editor object to make preference changes.
                        // All objects are from android.context.Context
                        final SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS_NAME_USER, 0).edit();
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(mAuth.getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            editor.putString(MainActivity.PREFS_USER, dataSnapshot.getValue(UserEntity.class).getEmail());
                                            editor.putString(MainActivity.PREFS_USER_UID, dataSnapshot.getKey());
                                            editor.putString(MainActivity.PREFS_USER_FIRSTNAME, dataSnapshot.getValue(UserEntity.class).getFirstname());
                                            editor.putString(MainActivity.PREFS_USER_LASTNAME, dataSnapshot.getValue(UserEntity.class).getLastname());
                                            editor.putString(MainActivity.PREFS_USER_EMERGENCY, dataSnapshot.getValue(UserEntity.class).getPhone());
                                            editor.apply();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            a_email.setText("");
                                            a_pw.setText("");
                                        } else {
                                            //invalide password
                                            a_pw.setError(getString(R.string.incorrect_pw));
                                            a_pw.requestFocus();
                                            a_pw.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d(TAG, "getAdminRights: onCancelled", databaseError.toException());
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "loginUserWithEmail: failure", task.getException());
                        a_pw.setError(getString(R.string.invalide_pw));
                        a_pw.requestFocus();
                        a_pw.setText("");
                    }
                }
            });
        }
    }

    private boolean validateForm(String email, String password) {
        // Reset errors.
        a_email.setError(null);
        a_pw.setError(null);

        boolean error = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            a_pw.setError(getString(R.string.invalide_pw));
            a_pw.setText("");
            focusView = a_pw;
            error = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            a_email.setError(getString(R.string.required));
            focusView = a_email;
            error = true;
        } else if (!isEmailValid(email)) {
            a_email.setError(getString(R.string.invalid_email));
            focusView = a_email;
            error = true;
        }

        if (error) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return !error;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
