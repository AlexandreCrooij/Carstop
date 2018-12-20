package com.driveby.alexa.carstop.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.entitiy.AlertEntitiy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private static final String TAG = "AlertsActivity";
    private DatabaseReference mDatabase;
    List<String> alerts = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_alert);
        setTitle(getString(R.string.alert));

        FirebaseDatabase.getInstance()
                .getReference("alerts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            alerts.clear();

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                AlertEntitiy entity = childSnapshot.getValue(AlertEntitiy.class);
                                entity.setUid(childSnapshot.getKey());
                                alerts.add(entity.getPlatenumber() + "\n" + entity.getDate());
                            }

                            //create a array adapter and add the marathon list to the list view
                            ListView listView = (ListView) findViewById(R.id.listStatisticsAlert);

                            arrayAdapter = new ArrayAdapter<String>(AlertsActivity.this, android.R.layout.simple_expandable_list_item_1, alerts);
                            listView.setAdapter(arrayAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "getAll: onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
