package com.application.android.partypooper.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Objects;

public class EditEventActivity extends PortraitActivity {

    /** ID of the event */
    private String eventID;

    /** Determines if an image is already uploading */
    private boolean uploading;

    /** Used to update the event inside the database */
    private HashMap<String, Object> edit;

    /** Informs the user the image is uploading to the database */
    private ProgressBar progressImage, progressSave;

    /** Displays the image uploaded by the user */
    private ImageView eventImage;

    /** Text views with on click listeners */
    private TextView start_date, end_date, upload, save;

    /** Information to be changed by the user */
    private EditText name, description, lnumber, lstreet, lcity;

    /** Containers to update with data */
    private RelativeLayout uploadContainer;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Reference to the result uri */
    private Uri mUri;

    /** Firebase reference to the event data */
    private DatabaseReference mEvent;

    /** Firebase reference to event pictures storage */
    private StorageReference sEvent;

    /** Date and Time for the start and end of the event */
    private int syear, smonth, sday, shour, smin;
    private int eyear, emonth, eday, ehour, emin;

    /** Date and Time dialog on click listeners */
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        initView();
        updateEventInformation();
        uploadImageListener(uploadContainer);

        hideKeyboardListener(name);
        hideKeyboardListener(lnumber);
        hideKeyboardListener(lstreet);
        hideKeyboardListener(lcity);
        hideKeyboardListener(description);

        dateTimeListener();
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        eventID = Objects.requireNonNull(b).getString("id");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEvent = FirebaseDatabase.getInstance().getReference().child("Events").child(eventID);

        sEvent = FirebaseStorage.getInstance().getReference("event_picture");

        name = findViewById(R.id.edit_event_name);
        description = findViewById(R.id.edit_event_description_text);
        lnumber = findViewById(R.id.edit_event_location_number);
        lstreet = findViewById(R.id.edit_event_location_street);
        lcity = findViewById(R.id.edit_event_location_city);

        start_date = findViewById(R.id.edit_event_start_date_time);
        end_date = findViewById(R.id.edit_event_end_date_time);
        upload = findViewById(R.id.edit_event_upload_text);
        save = findViewById(R.id.edit_event_save);

        eventImage = findViewById(R.id.edit_event_image);

        progressImage = findViewById(R.id.edit_event_upload_progress);
        progressSave = findViewById(R.id.edit_event_progress_bar);

        uploadContainer = findViewById(R.id.edit_event_upload_photo);

        progressImage.setVisibility(View.INVISIBLE);
        progressSave.setVisibility(View.INVISIBLE);

        edit = new HashMap<>();
        uploading = false;
    }

    private void updateEventInformation() {
        mEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                int month;
                String year, day, hour, min, date;

                String sdate = Objects.requireNonNull(event).getStart_date();
                String stime = Objects.requireNonNull(event).getStart_time();

                // date of the form dd/mm/yyyy
                day = sdate.substring(0,2); sday = Integer.parseInt(day);
                month = Integer.parseInt(sdate.substring(3,5)); smonth = month;
                year = sdate.substring(6,10); syear = Integer.parseInt(year);

                // time of the form hh:mm
                hour = stime.substring(0,2); shour = Integer.parseInt(hour);
                min = stime.substring(3,5); smin = Integer.parseInt(min);

                // date of the form "dd mm, at hh:mm"
                date = day+" "+getMonth(month)+", at "+hour+":"+min;
                start_date.setText(date);

                String edate = Objects.requireNonNull(event).getEnd_date();
                String etime = Objects.requireNonNull(event).getEnd_time();

                // date of the form dd/mm/yyyy
                day = edate.substring(0,2); eday = Integer.parseInt(day);
                month = Integer.parseInt(edate.substring(3,5)); emonth = month;
                year = edate.substring(6,10); eyear = Integer.parseInt(year);

                // time of the form hh:mm
                hour = etime.substring(0,2); ehour = Integer.parseInt(hour);
                min = etime.substring(3,5); emin = Integer.parseInt(min);

                // date of the form "End: dd mm, at hh:mm"
                date = "End: " + day+" "+getMonth(month)+", at "+hour+":"+min;
                end_date.setText(date);

                lnumber.setText(event.getNumber());
                lstreet.setText(event.getStreet());
                lcity.setText(event.getCity());
                name.setText(event.getName());
                description.setText(event.getDescription());

                if (event.getImageURL() != null) Glide.with(getApplicationContext()).load(event.getImageURL()).into(eventImage);
                else Glide.with(getApplicationContext()).load(R.drawable.default_banner).into(eventImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Listener to upload an event image.
     */
    private void uploadImageListener(RelativeLayout layout) {
        if (uploading) {
            showMessage("Image is uploading");
            return;
        }

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(2200,1000).
                    setCropShape(CropImageView.CropShape.RECTANGLE).start(EditEventActivity.this);
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
        progressImage.setVisibility(View.VISIBLE);
        progressSave.setVisibility(View.VISIBLE);
        upload.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        uploading = true;

        final StorageReference file = sEvent.child(
            System.currentTimeMillis() + "." + getFileExtension(mUri));

        final StorageTask<UploadTask.TaskSnapshot> uploadTask = file.putFile(mUri);

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
                    String imgURL = task.getResult().toString();

                    edit.put("imageURL", ""+imgURL);

                    mEvent.updateChildren(edit);
                } else {
                    showMessage("Image Upload Failed");
                }

                progressImage.setVisibility(View.INVISIBLE);
                progressSave.setVisibility(View.INVISIBLE);
                upload.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                uploading = false;
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
     * Converts a month in int form to a month in string form.
     * @param month index
     * @return corresponding string
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public void onClickEditEvent(View view) {
        finish();
    }

    public void onClickEditEventSave(View view) {
        if (uploading) {
            showMessage("Image is uploading");
            return;
        }

        final String _name = name.getText().toString();
        final String _number = lnumber.getText().toString();
        final String _street = lstreet.getText().toString();
        final String _city = lcity.getText().toString();

        if (_name.isEmpty()) {
            showMessage("Title is missing");
            return;
        }

        if (!getDateOrder()) {
            showMessage("End date is invalid");
            return;
        }

        if (_number.isEmpty() || _street.isEmpty() || _city.isEmpty()) {
            showMessage("Invalid address");
            return;
        }

        String day = ""+sday, month = ""+smonth, year = ""+syear, minute = ""+smin, hour = ""+shour;

        if (sday < 10) day = "0"+sday;
        if (smonth < 10) month = "0"+smonth;
        if (shour < 10) hour = "0"+shour;
        if (smin < 10) minute = "0"+smin;

        edit.put("start_date", day+"/"+month+"/"+year);
        edit.put("start_time", hour+":"+minute);
        edit.put("date_stamp",""+year+month+day+hour+minute);

        day = ""+eday;
        month = ""+emonth;
        year = ""+eyear;
        minute = ""+emin;
        hour = ""+ehour;

        if (eday < 10) day = "0"+eday;
        if (emonth < 10) month = "0"+emonth;
        if (ehour < 10) hour = "0"+ehour;
        if (emin < 10) minute = "0"+emin;

        edit.put("end_date", day+"/"+month+"/"+year);
        edit.put("end_time", hour+":"+minute);

        edit.put("name", _name);
        edit.put("number", _number);
        edit.put("street", _street);
        edit.put("city", _city);
        edit.put("description", description.getText().toString());

        mEvent.updateChildren(edit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                } else {
                    showMessage(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    private boolean getDateOrder() {
        // year
        if (syear > eyear) return false;

        // month
        if (smonth > emonth) return false;

        // day
        if (sday > eday) return false;

        //hour
        if (sday == eday && shour > ehour) return false;

        // minute
        if (sday == eday && shour == ehour && smin > emin) return false;

        return true;
    }

    /**
     * start_date text view listener that opens a date and time dialog.
     */
    private void dateTimeListener() {
        final TextView[] type = new TextView[1];

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type[0] = start_date;

                DatePickerDialog dialogDate = new DatePickerDialog(
                    EditEventActivity.this,
                    AlertDialog.THEME_HOLO_LIGHT,
                    mDateSetListener, syear, smonth, sday);

                Objects.requireNonNull(dialogDate.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

                dialogDate.show();
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type[0] = end_date;

                DatePickerDialog dialogDate = new DatePickerDialog(
                    EditEventActivity.this,
                    AlertDialog.THEME_HOLO_LIGHT,
                    mDateSetListener, eyear, emonth, eday);

                Objects.requireNonNull(dialogDate.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

                dialogDate.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                if (type[0] == start_date) {
                    sday = day;
                    smonth = month;
                    syear = year;
                    setDate(sday,smonth,syear,shour,smin,start_date);
                } else {
                    eday = day;
                    emonth = month;
                    eyear = year;
                    setDate(eday,emonth,eyear,ehour,emin,end_date);
                }

                TimePickerDialog dialogTime = new TimePickerDialog(
                    EditEventActivity.this,
                    AlertDialog.THEME_HOLO_LIGHT,
                    mTimeSetListener,shour,smin,true);

                Objects.requireNonNull(dialogTime.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

                dialogTime.show();
            }};

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (type[0] ==  start_date) {
                    shour = hourOfDay;
                    smin = minute;
                    setDate(sday,smonth,syear,shour,smin,start_date);
                } else {
                    ehour = hourOfDay;
                    emin = minute;
                    setDate(eday,emonth,eyear,ehour,emin,end_date);
                }
            }
        };
    }

    private void setDate (int d, int m, int y, int h, int min, TextView type) {
        String day = String.valueOf(d);
        String month = String.valueOf(m);
        String year = String.valueOf(y);
        String minute = String.valueOf(min);
        String hour = String.valueOf(h);

        if (d < 10) day = "0"+d;
        if (m < 10) month = "0"+m;
        if (h < 10) hour = "0"+h;
        if (min < 10) minute = "0"+min;

        if (type.equals(start_date)) {
            start_date.setText(getDate(day,m,minute,hour));
        } else {
            end_date.setText(String.format("End: %s", getDate(day, m, minute, hour)));
        }
    }

    /**
     * Display the date in the start_date Text View.
     */
    private String getDate(String day, int month, String min, String hour) {
        return day + " " + getMonth(month) + ", at " + hour + ":" + min;
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
     * Hides an open keyboard.
     * @param view of the activity
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)EditEventActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
