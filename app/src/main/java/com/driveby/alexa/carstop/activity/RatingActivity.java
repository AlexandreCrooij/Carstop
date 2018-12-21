package com.driveby.alexa.carstop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.entitiy.TripEntity;
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

                                if(dataSnapshot.exists()){

                                    Log.e(TAG, "onDataChange: "+ dataSnapshot.getValue());

                                    TripEntity tripEntity = dataSnapshot.getValue(TripEntity.class);
                                    dataSnapshot.getRef().child("wasSafe").setValue(true);
                                }
                                Log.e(TAG, "trip id: "+trip_id );
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