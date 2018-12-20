package com.driveby.alexa.carstop.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.driveby.alexa.carstop.activity.MainActivity;
import com.driveby.alexa.carstop.entitiy.UserEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.driveby.alexa.carstop.R;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;
    Button btn_save;
    Spinner spinner;

    private String user_firstname;
    private String user_lastname;
    private String user_emergency;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Get data of the user who using the app
        SharedPreferences pref = getContext().getSharedPreferences(MainActivity.PREFS_NAME_USER, 0);
        String user_email = pref.getString(MainActivity.PREFS_USER, null);
        user_firstname = pref.getString(MainActivity.PREFS_USER_FIRSTNAME, null);
        user_lastname = pref.getString(MainActivity.PREFS_USER_LASTNAME, null);
        user_emergency = pref.getString(MainActivity.PREFS_USER_EMERGENCY, null);

        //fill the input fields with the data of the user who is logged in
        final EditText firstname = (EditText) view.findViewById(R.id.txt_firstname);
        final EditText lastname = (EditText) view.findViewById(R.id.txt_lastname);
        final EditText emergency = (EditText) view.findViewById(R.id.txt_emergency);
        firstname.setText(user_firstname);
        lastname.setText(user_lastname);
        emergency.setText(user_emergency);

        //use the spinner with the array string of the language
        spinner = (Spinner) view.findViewById(R.id.sp_language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get selected position of the spinner
                //and get the language to change the language of the device
                int selected = spinner.getSelectedItemPosition();
                String language = "en";
                if(selected == 0){
                    language = "de";
                } else if (selected == 1){
                    language = "en";
                } else {
                    language = "fr";
                }
                changeLangugae(language);

                //update stored preferences
                SharedPreferences.Editor preferences = getContext().getSharedPreferences(MainActivity.PREFS_NAME_USER, 0).edit();
                String newFirstname = "";
                String newLastname = "";
                String newEmergency = user_emergency;

                //update the user data
                if(isPhoneValid(emergency.getText().toString())){
                    newFirstname = firstname.getText().toString();
                    newLastname = lastname.getText().toString();
                    newEmergency = emergency.getText().toString();
                    changeUserData(newFirstname, newLastname, newEmergency);
                } else {
                    newFirstname = firstname.getText().toString();
                    newLastname = lastname.getText().toString();
                    changeUserData(newFirstname, newLastname, null);
                }


                preferences.putString(MainActivity.PREFS_USER_FIRSTNAME, newFirstname);
                preferences.putString(MainActivity.PREFS_USER_LASTNAME, newLastname);
                preferences.putString(MainActivity.PREFS_USER_EMERGENCY, newEmergency);

                preferences.apply();

                //start the main activity
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //set the position of the spinner by the language who was last selected when the phone are running
        //so the selected language of the spinner is the same as the language of the device
        sharedPreferences = getContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String lang = sharedPreferences.getString(MainActivity.PREFS_LNG, null);
        int select;
        if(lang.equalsIgnoreCase("de")){
            select = 0;
        } else if(lang.equalsIgnoreCase("fr")){
            select = 2;
        } else {
            select = 1;
        }
        spinner.setSelection(select);

        return view;
    }

    public void changeLangugae(String language) {
        //change the language of application
        //by changing the local language of the device
        //and save the language to the new state of the stored preference
        Locale myLocale;
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        myLocale = new Locale(language);
        sharedPreferences.edit().putString(MainActivity.PREFS_LNG, language).apply();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void changeUserData(final String newFirstname, final String newLastname, final String newEmergency){
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserEntity entity = dataSnapshot.getValue(UserEntity.class);
                            entity.setUid(dataSnapshot.getKey());

                            entity.setFirstname(newFirstname);
                            entity.setLastname(newLastname);
                            if(newEmergency != null){
                                entity.setPhone(newEmergency);
                            }

                            mDatabase.child("users").child(getUID()).updateChildren(entity.toMap());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "getAll: onCancelled", databaseError.toException());
                    }
                });
    }

    private String getUID(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_NAME_USER, 0);
        String uid = sharedPreferences.getString(MainActivity.PREFS_USER_UID, null);
        return uid;
    }

    private boolean isPhoneValid(String phone){
        return (Patterns.PHONE.matcher(phone).matches() && phone.length() == 12) ;
    }

}

