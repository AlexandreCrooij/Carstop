package com.example.alexa.carstop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.alexa.carstop.R;
import com.example.alexa.carstop.entitiy.TripEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingActivity extends AppCompatActivity {

    private static final String TAG = "RatingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        setTitle(getString(R.string.rating));

        final String trip_id= getIntent().getStringExtra("TRIP_ENTITY");

        ImageView thumbUp = (ImageView)findViewById(R.id.thumbUp);
        thumbUp.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference("trips")
                        .child(trip_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                                snapshot.getRef().child("wasSafe").setValue(true);
//
//                                }


                                // Please check this part for the thumbsup ignition

                                if(dataSnapshot.exists()){

                                    Log.e(TAG, "onDataChange: "+ dataSnapshot.getValue());

                                    TripEntity tripEntity = dataSnapshot.getValue(TripEntity.class);
                                    dataSnapshot.getRef().child("wasSafe").setValue(true);
                                    System.out.println(tripEntity);
                                }



                                Log.e(TAG, "trip id: "+trip_id );
                                //TODO: MAYURA REDIRECTION NOTES





                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG,"fail");
                            }

                        });


                Intent intent = new Intent(v.getContext(), NoteActivity.class);
                String trip_id= getIntent().getStringExtra("TRIP_ENTITY");
                intent.putExtra("TRIP_ENTITY", trip_id);
                startActivity(intent);
            }
        });

        ImageView thumbDown = (ImageView)findViewById(R.id.thumbDown);
        thumbDown.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                //TODO: MAYURA REDIRECTON NOTES

                Intent intent = new Intent(v.getContext(), NoteActivity.class);
                String trip_id= getIntent().getStringExtra("TRIP_ENTITY");
                intent.putExtra("TRIP_ENTITY", trip_id);
                startActivity(intent);
            }
        });
    }
}