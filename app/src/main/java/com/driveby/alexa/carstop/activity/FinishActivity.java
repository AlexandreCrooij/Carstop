package com.driveby.alexa.carstop.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.entitiy.TripEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinishActivity extends AppCompatActivity {

    private static final String TAG = "FinishActivity";
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private DateFormat dateFormat;
    private String trip_id;

    private String message = "Mathis Fux eats a Gipfeli";
    private String telNr = "+41795173505";
    private int my_permission_request_send_sms = 1;
    private static final int REQUEST_READ_PHONE_STATE= 1;

    private String mySentMessage = "SMS_SENT";
    private String delivered = "SMS_DELIVERED";

    private PendingIntent sentPI;
    private PendingIntent deliveredPI;
    private BroadcastReceiver smsSendReceiver;
    private BroadcastReceiver smsDeliveredReciever;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Toast.makeText(FinishActivity.this, "Granted!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        smsSendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(FinishActivity.this, "SMS Sent!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(FinishActivity.this, "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(FinishActivity.this, "No service!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(FinishActivity.this, "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(FinishActivity.this, "Radio off!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(FinishActivity.this, "SMS delivered!", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(FinishActivity.this, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(smsSendReceiver, new IntentFilter(mySentMessage));
        registerReceiver(smsDeliveredReciever, new IntentFilter(delivered));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        setTitle(getString(R.string.finish));
        trip_id= getIntent().getStringExtra("TRIP_ENTITY");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //Important for SMS-Feedback
        Intent sendIntent = new Intent(mySentMessage);
        Intent deliveredIntent = new Intent(delivered);
        sentPI = PendingIntent.getBroadcast(this, 0, sendIntent,0);
        deliveredPI = PendingIntent.getBroadcast(this, 0 , deliveredIntent, 0);

        
        ImageView emergency = (ImageView)findViewById(R.id.emergency);
        emergency.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                //WINDOW
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(FinishActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(FinishActivity.this);
                }
                builder.setTitle("Send alert")
                        .setMessage("Are you sure you want to send a sms?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                System.out.println("HELLOOOOO");
                                Log.e(TAG, "HELLOOOO");

                                int permissionCheck = ContextCompat.checkSelfPermission(FinishActivity.this, Manifest.permission.READ_PHONE_STATE);

                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(FinishActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE );
                                } else {
                                    if(ContextCompat.checkSelfPermission(FinishActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(FinishActivity.this, new String[]{Manifest.permission.SEND_SMS}, my_permission_request_send_sms);
                                    }
                                    else{
                                        SmsManager sms = SmsManager.getDefault();
                                        sms.sendTextMessage(telNr, null, message, sentPI, deliveredPI);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        Button btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //start rating activity
                unregisterReceiver(smsDeliveredReciever);
                unregisterReceiver(smsSendReceiver);
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