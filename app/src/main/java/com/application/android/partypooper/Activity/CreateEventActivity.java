package com.application.android.partypooper.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.android.partypooper.Fragment.CreateEventFragment;
import com.application.android.partypooper.Fragment.EventSelectionFragment;
import com.application.android.partypooper.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CreateEventActivity extends AppCompatActivity {

  private ImageView closeActivity;

  private String timeStamp;
  private String mImageURL;

  private Uri mImageUri;
  private FirebaseUser mUser;
  private StorageTask uploadTask;
  private StorageReference storageRef;
  private DatabaseReference dbRef;

  private HashMap event;

  @Override
  protected void onStop() {
    event.put("time_stamp",timeStamp);
    event.put("host",mUser.getUid());

    dbRef.updateChildren(event);

    //dbRef.removeValue();

    super.onStop();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);

    updateFragment(new EventSelectionFragment());

    event = new HashMap();

    mUser = FirebaseAuth.getInstance().getCurrentUser();
    storageRef = FirebaseStorage.getInstance().getReference("event_picture");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
    timeStamp = dateFormat.format(new Date());

    dbRef = FirebaseDatabase.getInstance().getReference().child("Events").
            child(timeStamp + "?" + mUser.getUid());

    closeActivityListener();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      mImageUri = result.getUri();
      uploadImage();
    } else {
      showMessage("Something Went Wrong");
    }
  }

  private void updateFragment(Fragment frag) {
    getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
  }

  private void closeActivityListener () {
    closeActivity = findViewById(R.id.event_close);

    closeActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
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
            mImageURL = downloadUri.toString();

            event.put("imageURL", ""+mImageURL);

            dbRef.updateChildren(event);
            progress.dismiss();
          } else {
            showMessage("Image Upload Failed");
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          progress.dismiss();
        }
      });
    }
  }

  private void showMessage(String s) {
    Toast.makeText(this,s, Toast.LENGTH_LONG).show();
  }

  public FirebaseUser getmUser() {
    return mUser;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void addValueDatabase(String name, String value) {
    event.put(name,value);
  }
}
