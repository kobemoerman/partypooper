package com.application.android.partypooper.Activity;

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

import com.application.android.partypooper.Fragment.EventInformationFragment;
import com.application.android.partypooper.Fragment.EventThemeFragment;
import com.application.android.partypooper.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

    /** Unique id of the event */
    private String eventID;

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

    /** Reference to the current event in Events */
    private DatabaseReference mEvent;

    /** Reference to the current event in Members  */
    private DatabaseReference mMembers;

    /** Firebase reference to all events */
    private DatabaseReference refEvents;

    /** Firebase reference to all user events */
    private DatabaseReference refInvited;

    /** Firebase reference to all event users */
    private DatabaseReference refMembers;

    /** Firebase reference to all friends of a user */
    private DatabaseReference refFriends;

    /** Firebase reference to all users */
    private DatabaseReference refUsers;

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
        updateFragment(new EventThemeFragment(),"fragment/EventTheme");
    }

    /**
     * Initialises the event and members list.
     * Adds the time stamp and current user id to the event hash map.
     */
    private void initView() {
        event = new HashMap<>();

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

        eventID = timeStamp + "?" + mUser.getUid();

        refFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid());
        refEvents = FirebaseDatabase.getInstance().getReference().child("Events");
        refMembers = FirebaseDatabase.getInstance().getReference().child("Members");
        refInvited = FirebaseDatabase.getInstance().getReference().child("Invited");
        refUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mEvent = refEvents.child(eventID);
        mMembers = refMembers.child(eventID);

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
     * Called when the user has selected an image.
     * @param requestCode the request code passed to startActivityForResult.
     * @param resultCode RESULT_OK if successful or RESULT_CANCELED if the operation failed
     * @param data an Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mUri = result.getUri();
            if (mUri != null) {
                uploadImage();
            }
        } else {
            showMessage("Something Went Wrong");
        }
    }

    /**
     * Extends the file name.
     * @param uri reference to an image resource
     * @return file extension
     */
    private String getFileExtension (Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     * Uploads the image to the Firebase storage and save the url to the event map.
     */
    private void uploadImage () {
        final EventInformationFragment frag = (EventInformationFragment)
            getSupportFragmentManager().findFragmentByTag("fragment/EventInformation");

        frag.setVisible();

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

                    addItem("imageURL", ""+mURL);

                    frag.setEventImage();
                } else {
                    showMessage("Image Upload Failed");
                }
                frag.setInvisible();
            }
        });
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     */
    public void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    /**
     * Replace the current fragment with the one to be displayed.
     * @param frag fragment to be displayed
     */
    public void updateFragment(Fragment frag, String TAG) {
        getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag, TAG).commit();
    }

    /**
     * Returns the value of the item.
     * @param id to be fetched in the event hash map
     * @return the string value of the id
     */
    public String getItem(String id) {
        if (!event.containsKey(id)) {
            return null;
        }

        return event.get(id).toString();
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
     * Get the image uri that was uploaded.
     * @return mUri
     */
    public Uri getmUri() {
        return mUri;
    }

    /**
     * Firebase reference to the event in Events.
     * @return mEvent
     */
    public DatabaseReference getmEvent() {
        return mEvent;
    }

    /**
     * Firebase reference to the event in Members.
     * @return mMembers
     */
    public DatabaseReference getmMembers() {
        return mMembers;
    }

    /**
     * Firebase reference to all events.
     * @return refEvents
     */
    public DatabaseReference getRefEvents() {
        return refEvents;
    }

    /**
     * Firebase reference to all members.
     * @return refMembers
     */
    public DatabaseReference getRefMembers() {
        return refMembers;
    }

    /**
     * Firebase reference to all friends.
     * @return refFriends
     */
    public DatabaseReference getRefFriends() {
        return refFriends;
    }

    /**
     * Firebase reference to all users.
     * @return refUsers
     */
    public DatabaseReference getRefUsers() {
        return refUsers;
    }
}
