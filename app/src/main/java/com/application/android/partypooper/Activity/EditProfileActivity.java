package com.application.android.partypooper.Activity;

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
import android.widget.ProgressBar;
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
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity{

    private ProgressBar progress;
    private ImageView userImage;
    private TextView save;
    private MaterialEditText username, gender, status;

    private HashMap<String, Object> edit;

    private Uri mUri;

    private FirebaseUser mUser;
    private StorageReference mStorage;
    private DatabaseReference refUser;
    private StorageTask<UploadTask.TaskSnapshot> mTask;

    /**
     *
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();

        refUser.addValueEventListener(new ValueEventListener() {
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

    }

    private void initView() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference("profile_picture");
        refUser = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        save = findViewById(R.id.edit_profile_save);
        username = findViewById(R.id.edit_profile_username);
        gender = findViewById(R.id.edit_profile_gender);
        status = findViewById(R.id.edit_profile_status);
        userImage = findViewById(R.id.edit_profile_image);
        progress = findViewById(R.id.edit_profile_progress_bar);

        progress.setVisibility(View.INVISIBLE);

        edit = new HashMap<>();
    }

    public void onClickCloseEdit(View view) {
        finish();
    }

    public void onClickUpdateEdit(View view) {
        CropImage.activity().setAspectRatio(1,1).
                setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
    }

    public void onClickSaveEdit(View view) {
        String u = username.getText().toString();
        String g = gender.getText().toString();
        String s = status.getText().toString();

        updateProfile(u,g,s);
        finish();
    }

    private void updateProfile(String username, String gender, String status) {
        edit.put("username",username);
        edit.put("gender",gender);
        edit.put("status",status);

        refUser.updateChildren(edit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;
            mUri = result.getUri();
            uploadImage();
        } else {
            showMessage("Something Went Wrong");
        }
    }

    private void uploadImage () {
        progress.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);

        if (mUri == null) return;

        final StorageReference file = mStorage.child(System.currentTimeMillis() + "." + getFileExtension(mUri));

        mTask = file.putFile(mUri);
        mTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());

                return file.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (!task.isSuccessful()) {
                    showMessage("Image Upload Failed");
                    return;
                }

                Uri downloadUri = task.getResult();
                String imgURL = downloadUri.toString();

                edit.put("imgURL", ""+imgURL);
                refUser.updateChildren(edit);

                progress.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                progress.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getFileExtension (Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showMessage(String s) {
        Toast.makeText(EditProfileActivity.this,s,Toast.LENGTH_LONG).show();
    }
}
