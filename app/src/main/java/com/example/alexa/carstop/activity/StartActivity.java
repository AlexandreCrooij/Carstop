package com.example.alexa.carstop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.alexa.carstop.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class StartActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgess;
    private Bundle extras;
    private Bitmap bitmap;
    private Date time;
    private EditText plateNumber;
    private ImageView uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = FirebaseStorage.getInstance().getReference();
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
            time = Calendar.getInstance().getTime();
            if(bitmap != null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imagesRef = storageRef.child("Photos").child("filename" + time.getTime());

                UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),"Sending failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgess.dismiss();
                    }
                });
            }
        }
    }
}
