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

import com.application.android.partypooper.Fragment.CreateInformationFragment;
import com.application.android.partypooper.Fragment.CreateThemeFragment;
import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateEventActivity extends AppCompatActivity {

    /** Format used for the time stamp */
    private static final String FORMAT = "yyyy:MM:dd:hh:mm:ss";

    /** Determines whether a picture is uploading to the database */
    private boolean uploading;

    /** Unique id of the event */
    private String eventID;

    /** Current time stamp */
    private String timeStamp;

    /** Stores data about the event */
    private HashMap<String, Object> event;

    /** Stores all user mRecommendations */
    private ArrayList<Recommendation> mRecommendations;

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

    /** Firebase reference to all event recommendations */
    private DatabaseReference refRecommendation;

    /** Firebase reference to event pictures storage */
    private StorageReference sEvent;

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initFirebase();
        initView();
        updateFragment(new CreateThemeFragment(),"fragment/EventTheme");
    }

    /**
     * Initialises the event and members list.
     * Adds the time stamp and current user id to the event hash map.
     */
    private void initView() {
        event = new HashMap<>();
        mRecommendations = new ArrayList<>();

        hostUsername();
        addItem("host", mUser.getUid());
        addItem("time_stamp", timeStamp);

        uploading = false;
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
        refRecommendation = FirebaseDatabase.getInstance().getReference().child("Recommendation").child(eventID);

        mEvent = refEvents.child(eventID);
        mMembers = refMembers.child(eventID);

        sEvent = FirebaseStorage.getInstance().getReference("event_picture");
    }

    /**
     * On click listener for the finish create event button.
     * Updates the event with the mEvent hash map.
     * Opens the event activity and kills this activity.
     */
    public void onClickFinishCreateEvent(View view) {
        mEvent.updateChildren(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI();
                    updateInvitedDataBase();
                    updateRecommendationDatabase();
                } else {
                    showMessage(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    /**
     * On click listener for the close activity image view.
     * Removes all users that have been invited.
     * Kills this activity.
     * @param view view of this activity
     */
    public void onClickCloseCreateEvent(View view) {
        if (uploading) return;

        finish();
        clearEvent();
        mMembers.removeValue();
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
        final CreateInformationFragment frag = (CreateInformationFragment)
            getSupportFragmentManager().findFragmentByTag("fragment/EventInformation");

        frag.setVisible();

        final StorageReference file = sEvent.child(
            System.currentTimeMillis() + "." + getFileExtension(mUri));

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
     * Adds the event to the invited users from the Firebase.
     */
    public void updateInvitedDataBase() {
        mMembers.child(mUser.getUid()).setValue(false);
        refInvited.child(mUser.getUid()).child(eventID).setValue(false);

        mMembers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    refInvited.child(snp.getKey()).child(eventID).setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates a Firebase reference to all event recommendations
     */
    private void updateRecommendationDatabase() {
        for (Recommendation r : mRecommendations) {
            refRecommendation.child(r.getItem()).child("total").setValue(r.getAmount());
            refRecommendation.child(r.getItem()).child("brought").setValue(0);
        }
    }

    /**
     * Adds the username value of the current user creating the event.
     */
    private void hostUsername() {
        refUsers.child(mUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                addItem("host_username",username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
     * Launches the Event Activity and kills the current one.
     */
    private void updateUI() {
        Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
        eventIntent.putExtra("id",eventID);
        startActivity(eventIntent);
        finish();
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

        return Objects.requireNonNull(event.get(id)).toString();
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
     * Returns the list of all recommendations.
     * @return mRecommendations
     */
    public ArrayList<Recommendation> getmRecommendations() {
        return mRecommendations;
    }

    /**
     * Sets the list of recommendations.
     * @param mRecommendations new list of recommendations
     */
    public void setmRecommendations(ArrayList<Recommendation> mRecommendations) {
        this.mRecommendations = mRecommendations;
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

    /**
     * Returns whether an image is being uploaded.
     * @return uploading
     */
    public boolean isUploading() {
        return uploading;
    }

    /**
     * Set the value when image is/isn't being uploaded
     * @param uploading value to set
     */
    public void setUploading(boolean uploading) {
        this.uploading = uploading;
    }
}
