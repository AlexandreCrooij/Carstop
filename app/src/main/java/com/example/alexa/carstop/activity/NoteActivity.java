package com.example.alexa.carstop.activity;

import com.example.alexa.carstop.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.alexa.carstop.entitiy.TripEntity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";

    // Create the UI elements reference

    private EditText editTextNote;
    private Button btnSubmit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //setTitle(getString(R.string.));


        final String trip_id= getIntent().getStringExtra("TRIP_ENTITY");
        Log.e(TAG, "onCreate: This is done "+trip_id);
        editTextNote = (EditText) findViewById(R.id.editTextNote);


        Button btnSubmit =  findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener(){



            public void onClick(View v) {
                Log.e(TAG, "TRIP ID: "+ trip_id);

                final String note = editTextNote.getText().toString();

                if(note.isEmpty()){

                    Log.e(TAG, "String Note is empty" );
                }
                if(editTextNote.getText().toString().isEmpty()){
                    Log.e(TAG, "Note is empty" );

                }
                else {

                    FirebaseDatabase.getInstance()
                            .getReference("trips")
                            .child(trip_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // Test for the existence of certain keys within a DataSnapshot

                                    if (dataSnapshot.exists()) {

                                        Log.e(TAG, "onDataChange: "+ dataSnapshot.getValue());

                                        TripEntity tripEntity = dataSnapshot.getValue(TripEntity.class);
                                        tripEntity.setNote(note);
                                        dataSnapshot.getRef().child("note").setValue(note);
                                        System.out.println(tripEntity);

                                        // TODO Next screen or intent
                                      //  Toast.makeText(getBaseContext(),"Thank you for your comments", Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(),"Thank you for your comments", Toast.LENGTH_LONG).show();
                                        //finish();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);







                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "fail");
                                }
                            });

                }
            }
        });

    }
}
