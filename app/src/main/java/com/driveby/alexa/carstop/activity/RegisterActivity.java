package com.driveby.alexa.carstop.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(getString(R.string.register));

        Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv_firstname = (EditText) findViewById(R.id.tv_firstname_register);
                EditText tv_lastname = (EditText) findViewById(R.id.tv_lastname_register);
                final EditText tv_email = (EditText) findViewById(R.id.tv_email_register);
                EditText tv_password = (EditText) findViewById(R.id.tv_password_register);
                EditText tv_check = (EditText) findViewById(R.id.tv_password_c_register);

                EditText tv_phone = (EditText) findViewById(R.id.tv_phone_register);
                final String phone = tv_phone.getText().toString();

                final String firstname = tv_firstname.getText().toString();
                final String lastname = tv_lastname.getText().toString();
                final String email = tv_email.getText().toString();
                final String password = tv_password.getText().toString();
                pw = password;
                String pw_check = tv_check.getText().toString();

                View focusView = null;
                tv_email.setError(null);
                tv_password.setError(null);

                if(!firstname.equals("") && !lastname.equals("") && !email.equals("") && !password.equals("") && !pw_check.equals("") &&  !phone.equals("")){
                    //every field filled out
                    if(isEmailValid(email)){
                        //email valid

                        //phone validation
                        if(isPhoneValid(phone)) {

                            if (password.equals(pw_check)) {
                                //pw are equals
                                if (isPasswordValid(password)) {
                                    //pw is valid
                                    FirebaseDatabase.getInstance()
                                            .getReference("users")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        boolean exists = false;

                                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                            UserEntity entity = childSnapshot.getValue(UserEntity.class);
                                                            entity.setUid(childSnapshot.getKey());
                                                            if (entity.getEmail().equalsIgnoreCase(email)) {
                                                                exists = true;
                                                                break;
                                                            }
                                                        }

                                                        if (!exists) {
                                                            //user doesn't exists
                                                            //addUser(firstname, lastname, email, password);
                                                            UserEntity userEntity = new UserEntity();
                                                            userEntity.setFirstname(firstname);
                                                            userEntity.setLastname(lastname);
                                                            userEntity.setEmail(email);
                                                            userEntity.setPhone(phone);
                                                            addUser(userEntity);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.d(TAG, "getAll: onCancelled", databaseError.toException());
                                                }
                                            });
                                } else {
                                    tv_password.setError(getString(R.string.invalide_pw));
                                    tv_password.setText("");
                                    tv_check.setText("");
                                    focusView = tv_password;
                                    focusView.requestFocus();
                                }
                            } else {
                                //pw aren't equals
                                tv_password.setError(getString(R.string.notMatch));
                                tv_password.setText("");
                                tv_check.setText("");
                                focusView = tv_password;
                                focusView.requestFocus();
                            }
                            // phone pattern not matching
                        } else {
                            if(phone.length() != 12){
                                tv_phone.setError("Please check the length");
                            }
                            else{
                                tv_phone.setError("Check the number");
                            }
                            focusView = tv_phone;
                            focusView.requestFocus();
                        }

                    } else {
                        tv_email.setError(getString(R.string.invalid_email));
                        focusView = tv_email;
                        focusView.requestFocus();
                    }
                } else {
                    //not filled out all required fields
                    Toast.makeText(getApplicationContext(), getString(R.string.requiredAll), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void addUser(final UserEntity user) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.getEmail(), pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: success");
                            addUserInFirebase(user);
                        } else {
                            Log.d(TAG, "createUserWithEmail: failure", task.getException());
                            setResponse(false);
                        }
                    }
                });
    }

    private void addUserInFirebase(UserEntity user) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "Firebase DB Insert failure!", databaseError.toException());
                            setResponse(false);
                            FirebaseAuth.getInstance().getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Rollback: User account deleted");
                                            } else {
                                                Log.d(TAG, "Rollback: signInWithEmail:failure", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Firebase DB Insert successful!");
                            setResponse(true);
                        }
                    }
                });
    }

    private void setResponse(Boolean response) {
        if (response) {
            Toast.makeText(getApplicationContext(), getString(R.string.addUser), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneValid(String phone){

        return (Patterns.PHONE.matcher(phone).matches() && phone.length() == 12) ;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
