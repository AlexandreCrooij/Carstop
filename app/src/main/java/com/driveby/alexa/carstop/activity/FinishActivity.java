package com.driveby.alexa.carstop.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.entitiy.TripEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinishActivity extends AppCompatActivity {

    private static final String TAG = "FinishActivity";
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private DateFormat dateFormat;
    private String trip_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        setTitle(getString(R.string.finish));
        trip_id= getIntent().getStringExtra("TRIP_ENTITY");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        Button btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //start rating activity
                Intent intent = new Intent(v.getContext(), RatingActivity.class);
                String trip_id = getIntent().getStringExtra("TRIP_ENTITY");
                intent.putExtra("TRIP_ENTITY", trip_id);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FinishActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            finish();
            return;
        }
        Toast.makeText(
                getBaseContext(),
                "Recording GPS Data...",Toast.LENGTH_LONG).show();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, locationListener);

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            final double longitude = loc.getLongitude();
            final double latitude = loc.getLatitude();
            final Date date = new Date();

            FirebaseDatabase.getInstance()
                    .getReference("trips")
                    .child(trip_id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                TripEntity tripEntity = dataSnapshot.getValue(TripEntity.class);
                                dataSnapshot.getRef().child("gps").child(dateFormat.format(date)).child("longitude").setValue(longitude);
                                dataSnapshot.getRef().child("gps").child(dateFormat.format(date)).child("latitude").setValue(latitude);
                                System.out.println(tripEntity);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG,"fail");
                        }
                    });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
    }