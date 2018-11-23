package com.example.alexa.carstop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alexa.carstop.entitiy.CarEntity;
import com.example.alexa.carstop.entitiy.TripEntity;
import com.example.alexa.carstop.entitiy.UserEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.alexa.carstop.R;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.UUID;

import static com.example.alexa.carstop.activity.MainActivity.PREFS_USER_UID;

public class StartActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgess;
    private Bundle extras;
    private Bitmap bitmap;
    private Date time;
    private EditText plateNumber;
    private ImageView uploadImage;
    private DateFormat dateFormat;
    private SharedPreferences sharedPreferences;
    private Query query;
    private DatabaseReference reference;
    private DatabaseReference root;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME_USER, 0);
        mStorage = FirebaseStorage.getInstance().getReference();
        uid = sharedPreferences.getString(MainActivity.PREFS_USER_UID, null);
        dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        setContentView(R.layout.activity_start);
        mProgess = new ProgressDialog(this);
    }


    public void onClickBtnImageUpload(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            uploadImage = findViewById(R.id.imageViewUpload);
            extras = data.getExtras();
            bitmap = (Bitmap) data.getExtras().get("data");
            uploadImage.setImageBitmap(bitmap);
        }
    }

    public void onClickBtnConfirm(View view){
        plateNumber = findViewById(R.id.editTextPlatenumber);
        if( TextUtils.isEmpty(plateNumber.getText())){
            plateNumber.setError( "Platenumber is required!" );
        }else{
            mProgess.setMessage("Upload...");
            mProgess.show();
            time = new Date();
            String nameOfPhoto = null;
            String platenumber = plateNumber.getText().toString().toUpperCase();
            if(bitmap != null){
                nameOfPhoto = uid+time.getTime();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imagesRef = storageRef.child("Photos").child(nameOfPhoto);

                UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),"Sending failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            final CarEntity carEntity = new CarEntity();
            carEntity.setNumbersplate(platenumber);
            final TripEntity tripEntity = new TripEntity();
            tripEntity.setStartDate(dateFormat.format(time));
            tripEntity.setIdCar(carEntity.getNumbersplate());
            tripEntity.setIdUser(uid);
            tripEntity.setUid(UUID.randomUUID().toString());
            if(nameOfPhoto != null) {
                tripEntity.setImageUrl(nameOfPhoto);
            }

            query = root.child("cars").child(carEntity.getNumbersplate());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //nothing happens
                    }
                    else{
                        //insert
                        FirebaseDatabase.getInstance()
                                .getReference("cars")
                                .child(carEntity.getNumbersplate())
                                .setValue(carEntity, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            System.out.println("Error");
                                        } else {

                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance()
                    .getReference("trips")
                    .child(tripEntity.getUid())
                    .setValue(tripEntity, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Error");
                            } else {

                            }
                        }
                    });
            mProgess.dismiss();

            //start finish activity
            Intent intent = new Intent(this, FinishActivity.class);
            intent.putExtra("TRIP_ENTITY", tripEntity.getUid());
            startActivity(intent);
        }
    }
}
