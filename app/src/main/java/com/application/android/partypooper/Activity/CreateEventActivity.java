package com.application.android.partypooper.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.application.android.partypooper.Fragment.EventThemeFragment;
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
import java.util.*;

public class CreateEventActivity extends AppCompatActivity {

    /** Format used for the time stamp */
    private static final String FORMAT = "yyyy:MM:dd:hh:mm:ss";

    /** Current time stamp */
    private String timeStamp;

    /** Stores data about the event */
    private HashMap<String, Object> event;

    /** Reference to the result uri */
    private Uri mUri;

    /** Url of the image inside the storage */
    private String mURL;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** All users invited to the event */
    private List<String> mMembers;

    /** Reference the the current event */
    private DatabaseReference mEvent;

    /** Firebase reference to all events */
    private DatabaseReference refEvents;

    /** Firebase reference to all members */
    private DatabaseReference refMembers;

    /** Firebase reference to event pictures storage */
    private StorageReference sEvent;

    /**
     * On create method of the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initFirebase();
        initView();
        updateFragment(new EventThemeFragment());
    }

    /**
     * Initialises the event and members list.
     * Adds the time stamp and current user id to the event hash map.
     */
    private void initView() {
        event = new HashMap<>();
        mMembers = new ArrayList<>();

        addItem("host",mUser.getUid());
        addItem("time_stamp",timeStamp);
    }

    /**
     * Initialise Firebase and make queries.
     */
    private void initFirebase() {
        timeStamp = new SimpleDateFormat(FORMAT,Locale.FRANCE).format(new Date());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        refEvents = FirebaseDatabase.getInstance().getReference().child("Events");
        refMembers = FirebaseDatabase.getInstance().getReference().child("Members");

        mEvent = refEvents.child(timeStamp + "?" + mUser.getUid());

        sEvent = FirebaseStorage.getInstance().getReference("event_picture");

    }

    /**
     * On click listener for the finish create event button.
     * Updates the event with the mEvent hash map.
     * Opens the event activity and kills this activity.
     * @param view view of the activity
     */
    public void onClickFinishCreateEvent(View view) {
        mEvent.updateChildren(event);
    }

    /**
     * On click listener for the close activity image view.
     * Removes the event and all references to it.
     * Kills this activity.
     * @param view view of this activity
     */
    public void onClickCloseCreateEvent(View view) {
        clearEvent();
        finish();
    }

    /**
     * Replace the current fragment with the one to be displayed.
     * @param frag fragment to be displayed
     */
    public void updateFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
    }

    /**
     * Adds an item to the event hash map.
     * @param id of the item.
     * @param item to be saved in the database
     */
    public void addItem(String id, String item) {
        event.put(id,item);
    }

    /**
     * Remove an item from the event hash map.
     * @param id item to be removed
     */
    public void removeItem(String id) {
        event.remove(id);
    }

    /**
     * Clears the even hash map.
     */
    public void clearEvent() {
        if (event != null) {
            event.clear();
        }
    }

    /**
     * Adds a user id to the invitee list.
     * @param id to be added
     */
    public void addFriend(String id) {
        mMembers.add(id);
    }

    /**
     * Removes a user id from the invitee list.
     * @param id to be removed
     */
    public void removeFriend(String id) {
        mMembers.remove(id);
    }

    /**
     * Clears the invitee list.
     */
    public void clearFriends() {
        mMembers.clear();
    }

    /**
     * Reference to the current user.
     * @return mUser
     */
    public FirebaseUser getmUser() {
        return mUser;
    }

    /**
     * Time stamp when this activity was created.
     * @return timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     */
    private void showMessage(String s) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mUri = result.getUri();
            uploadImage();
        } else {
            showMessage("Something Went Wrong");
        }
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

        if (mUri != null) {

            final StorageReference file = sEvent.child(System.currentTimeMillis() + "." + getFileExtension(mUri));

            StorageTask<UploadTask.TaskSnapshot> uploadTask = file.putFile(mUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return file.getDownloadUrl();
            }}).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    mURL = task.getResult().toString();

                    event.put("imageURL", ""+ mURL);

                    mEvent.updateChildren(event);
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
}
