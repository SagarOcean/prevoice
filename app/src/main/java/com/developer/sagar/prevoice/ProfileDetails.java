package com.developer.sagar.prevoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.HashMap;

public class ProfileDetails extends AppCompatActivity {
    private ImageView profileImage, picPhoto;
    private Uri imageUri;
    private EditText name,age,country;
    private DatabaseReference userRef;
    private StorageReference profileImageRef;
    private String downloadUrl;
    private SimpleArcLoader loader;
    private static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_profile);
        getSupportActionBar().hide();
        profileImage = findViewById(R.id.profile_img);
        picPhoto = findViewById(R.id.pic_image);
        name = findViewById(R.id.userName);
        age = findViewById(R.id.userAge);
        country = findViewById(R.id.userCountry);
        loader = findViewById(R.id.loader);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")){
                        startActivity(new Intent(ProfileDetails.this,HomeActivity.class));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void pickImage(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            picPhoto.setVisibility(View.GONE);
        }
    }

        public void goToHome (View view){
            final String getName = name.getText().toString();
            final String getAge = age.getText().toString();
            final String getCountry = country.getText().toString();
            if(imageUri == null){
                Toast.makeText(ProfileDetails.this,"Please select an image first",Toast.LENGTH_SHORT).show();

            }
            else if(getName.equals("")){
                Toast.makeText(getApplicationContext(),"Please write user name",Toast.LENGTH_SHORT).show();
            }
            else if(getAge.equals("")){
                Toast.makeText(getApplicationContext(),"Please write age",Toast.LENGTH_SHORT).show();
            }
            else if(getCountry.equals("")){
                Toast.makeText(getApplicationContext(),"Please write country name",Toast.LENGTH_SHORT).show();
            }

            else {
                loader.setVisibility(View.VISIBLE);
                loader.start();
                final StorageReference filePath = profileImageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                final UploadTask uploadTask = filePath.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl = task.getResult().toString();
                            HashMap<String,Object> profileMap = new HashMap<>();
                            profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            profileMap.put("name",getName);
                            profileMap.put("age",getAge);
                            profileMap.put("country",getCountry);
                            profileMap.put("image",downloadUrl);
                            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .updateChildren(profileMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(ProfileDetails.this,HomeActivity.class));
                                                loader.stop();
                                                Toast.makeText(getApplicationContext(),"Data submitted successfully",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }

        }
}
