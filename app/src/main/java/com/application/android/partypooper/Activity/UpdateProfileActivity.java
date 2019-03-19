package com.application.android.partypooper.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity{

    private ImageView close, userImage;
    private TextView updateImg, save;
    private MaterialEditText username, gender, status;

    private FirebaseUser currentUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        findActivityElements();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                gender.setText(user.getGender());
                status.setText(user.getStatus());
                if (user.getImgURL() != null) Glide.with(getApplicationContext()).load(user.getImgURL()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).
                        setCropShape(CropImageView.CropShape.OVAL).start(UpdateProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(username.getText().toString(),gender.getText().toString(),status.getText().toString());
                Toast.makeText(UpdateProfileActivity.this,"All Changes Saved",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateProfile(String username, String gender, String status) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        HashMap<String, Object> info = new HashMap<>();
        info.put("username",username);
        info.put("gender",gender);
        info.put("status",status);

        ref.updateChildren(info);
    }

    private String getFileExtension (Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage () {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Uploading");
        progress.show();

        if (mImageUri != null) {

            final StorageReference file = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            uploadTask = file.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imgURL = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

                        HashMap<String, Object> img = new HashMap<>();
                        img.put("imgURL", "" + imgURL);

                        ref.updateChildren(img);
                        progress.dismiss();
                    } else {
                        displayToastToUser("Image Upload Failed");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayToastToUser(e.getMessage());
                    progress.dismiss();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            uploadImage();
        } else {
            displayToastToUser("Something Went Wrong");
        }
    }

    private void displayToastToUser (String msg) {
        Toast.makeText(UpdateProfileActivity.this,msg,Toast.LENGTH_LONG).show();
    }

    private void findActivityElements() {
        close = findViewById(R.id.update_profile_close);
        save = findViewById(R.id.update_profile_save);
        updateImg = findViewById(R.id.update_profile_change_image);
        username = findViewById(R.id.update_profile_username);
        gender = findViewById(R.id.update_profile_gender);
        status = findViewById(R.id.update_profile_status);
        userImage = findViewById(R.id.update_profile_image);
    }
}
