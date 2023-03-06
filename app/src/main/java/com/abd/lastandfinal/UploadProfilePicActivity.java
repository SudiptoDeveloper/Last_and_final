package com.abd.lastandfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBarUPic;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authUpPic;

    private StorageReference storageRef;

    private FirebaseUser firebaseUserPic;

    private static final int PICK_IMAGE_REQUEST = 1;

    Uri uriImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("UploadProfilePicActivity");

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBarUPic = findViewById(R.id.progressBarPic);
        imageViewUploadPic = findViewById(R.id.imageView_profile_dp);


        authUpPic = FirebaseAuth.getInstance();
        firebaseUserPic = authUpPic.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("path/to/image.jpg");

        Uri uri = firebaseUserPic.getPhotoUrl();

        //set user current DP in imageView (if upload already). We will picasso imageView setImage.
        //Regular Uris.
        Picasso.get()
                .load(String.valueOf(storageRef))
                .into(imageViewUploadPic);

        // Choosing image to upload
        buttonUploadPicChoose.setOnClickListener(view -> {
            openFileChooser();
        });

        // Upload image
        buttonUploadPic.setOnClickListener(view -> {
            progressBarUPic.setVisibility(View.VISIBLE);
            uploadPic();
        });

    }

    private void uploadPic() {
        if (uriImage!= null){
            //save the image with the uid of the currently logged user
            StorageReference fileReference = storageRef.child(authUpPic.getCurrentUser().getUid()+ "."
                    + getFileExtention(uriImage));

            //Upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            Picasso.get()
                                    .load(uri)
                                    .into(imageViewUploadPic);
                            Uri downloadUri = uriImage;
                            firebaseUserPic = authUpPic.getCurrentUser();

                            // Finally set Display Image of the user after upload
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUserPic.updateProfile(profileUpdates);
                        }
                    });
                    progressBarUPic.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePicActivity.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UploadProfilePicActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePicActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } else {
            progressBarUPic.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePicActivity.this, "No File Selected", Toast.LENGTH_LONG).show();
        }

    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
/*
       if (id == R.id.menu_update_profile){
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(UserProfileActivity.this,"Settings",Toast.LENGTH_LONG).show();
        } else */if (id == R.id.menu_logout) {
            authUpPic.signOut();
            Toast.makeText(UploadProfilePicActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadProfilePicActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UploadProfilePicActivity.this,"Somethings went wrong!",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

}