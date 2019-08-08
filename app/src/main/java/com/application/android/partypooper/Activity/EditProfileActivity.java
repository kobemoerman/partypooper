package com.application.android.partypooper.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /** Strings representing the username, status, and gender of the user */
    private String u, g, s;

    /** Allows the user to save the information */
    private TextView save;

    /** Display a range of items for the user to choose from */
    private Spinner gender;

    /** Responsible to display the profile picture */
    private ImageView userImage;

    /** Responsible for displaying the username and status */
    private EditText username, status;

    /** Appears when the user saves the information */
    private ProgressBar progress;

    /** Stores the information the user wants to save */
    private HashMap<String, Object> edit;

    /** Reference to the result uri */
    private Uri mUri;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Firebase reference to user pictures storage */
    private StorageReference mStorage;

    /** Firebase reference to all users */
    private DatabaseReference refUser;

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();
        updateUserInformation();
        hideKeyboardListener(username);
        hideKeyboardListener(status);
    }

    /**
     * Initialises the activity view and Firebase references.
     */
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(this);
    }

    /**
     * Updates the information to be displayed on the activity.
     */
    private void updateUserInformation() {
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                status.setText(user.getStatus());

                if (user.getImgURL() != null) Glide.with(getApplicationContext()).load(user.getImgURL()).into(userImage);

                switch (user.getGender()){
                    case "Male":
                        gender.setSelection(1);
                        break;
                    case "Female":
                        gender.setSelection(2);
                        break;
                    default:
                        gender.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Listener to update the profile picture.
     * Opens an activity to pick a new image.
     * @param view of the activity
     */
    public void onClickUpdateEdit(View view) {
        CropImage.activity().setAspectRatio(1,1).
                setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
    }

    /**
     * Listener to save the new values and close the activity.
     * @param view of the activity
     */
    public void onClickSaveEdit(View view) {
        u = username.getText().toString();
        s = status.getText().toString();

        updateProfile();
        finish();
    }

    /**
     * Listener to discard the changes and close the activity.
     * @param view of the activity
     */
    public void onClickCloseEdit(View view) {
        finish();
    }

    /**
     * Sets the new values inside the Hash map.
     */
    private void updateProfile() {
        edit.put("username",u);
        edit.put("gender",g);
        edit.put("status",s);

        refUser.updateChildren(edit);
    }

    /**
     * Calls @hideKeyboard when the users touches outside the edit text.
     */
    private void hideKeyboardListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
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
        progress.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);

        final StorageReference file = mStorage.child(
            System.currentTimeMillis() + "." + getFileExtension(mUri));

        StorageTask<UploadTask.TaskSnapshot> uploadTask = file.putFile(mUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return file.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imgURL = downloadUri.toString();

                    edit.put("imgURL", ""+imgURL);

                    refUser.updateChildren(edit);
                } else {
                    showMessage("Image Upload Failed");
                }

                progress.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Hides an open keyboard.
     * @param view of the activity
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Display a toast on the screen.
     * @param s message to be displayed
     */
    private void showMessage(String s) {
        Toast.makeText(EditProfileActivity.this,s,Toast.LENGTH_LONG).show();
    }

    /**
     * Update the value from the spinner inside the Hash map.
     * @param parent the AdapterView where the selection happened
     * @param view the view within the AdapterView that was clicked
     * @param position the position of the view in the adapter
     * @param id the row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        g = parent.getItemAtPosition(position).toString();
    }

    /**
     * Callback method to be invoked when the selection disappears from this view.
     * @param parent the AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
