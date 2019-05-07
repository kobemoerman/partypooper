package com.application.android.partypooper.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.TimePickerDialog.*;
import android.app.DatePickerDialog.*;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Objects;

public class EventInformationFragment extends Fragment implements Events {

    /** TAG reference of this fragment */
    private final static String TAG = "fragment/EventInformation";

    /** Reference to the CreateEvent Activity */
    private CreateEventActivity act;

    /** Determines whether a picture is uploading to the database */
    private boolean uploading;

    /** Informs the user the image is uploading to the database */
    private ProgressBar progress;

    /** Buttons to navigate back and forth between fragments */
    private Button mNext, mBack;

    /** Displays the image uploaded by the user */
    private ImageView eventImage;

    /** Text views with on click listeners */
    private TextView date_time, upload;

    /** Information to be changed by the user */
    private EditText name, location, description;

    /** Containers to update with data */
    private RelativeLayout imageContainer, uploadContainer;

    /** Date and Time selected by the user */
    private int year, month, day, hour, min;

    /** Date and Time dialog on click listeners */
    private OnDateSetListener mDateSetListener;
    private OnTimeSetListener mTimeSetListener;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_information, container, false);

        initView(view);
        updateData();
        navigationListener(mBack, new EventThemeFragment(),"fragment/EventTheme");
        navigationListener(mNext, new EventInviteFragment(),"fragment/EventInvite");

        uploadImageListener(uploadContainer);
        uploadImageListener(imageContainer);

        dateTimeListener();

        return view;
    }

    /**
     * Initialises the fragment view, date and time.
     * @param view fragment_event_information
     */
    private void initView(View view) {
        Calendar cal = Calendar.getInstance();

        act = (CreateEventActivity) getActivity();
        assert act != null;

        mNext = view.findViewById(R.id.frag_event_next);
        mBack = view.findViewById(R.id.frag_event_back);
        date_time = view.findViewById(R.id.frag_event_date_time);
        name = view.findViewById(R.id.frag_event_name);
        location = view.findViewById(R.id.frag_event_location);
        description = view.findViewById(R.id.frag_event_description);
        uploadContainer = view.findViewById(R.id.frag_event_upload_photo);
        upload = view.findViewById(R.id.frag_event_upload_text);
        eventImage = view.findViewById(R.id.frag_event_image2);
        imageContainer = view.findViewById(R.id.frag_event_image);
        progress = view.findViewById(R.id.frag_event_upload_progress);

        eventImage.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        hour = cal.get(Calendar.HOUR_OF_DAY)+1;
        min = 0;

        uploading = false;
    }

    /**
     * Updates the data if the user has already added information.
     */
    private void updateData() {
        String name = act.getItem("name");
        String location = act.getItem("location");
        String description = act.getItem("description");
        String date_time = act.getItem("time");

        if (name != null) {
          this.name.setText(name);
        }

        if (location != null) {
          this.location.setText(location);
        }

        if (description != null) {
          this.description.setText(description);
        }

        if (date_time != null) {
          this.date_time.setText(date_time);
        }

        setEventImage();
    }

    /**
     * Listener to upload an event image.
     */
    private void uploadImageListener(RelativeLayout layout) {
        if (uploading) {
          act.showMessage("Image is uploading");
          return;
        }

        layout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            CropImage.activity().setAspectRatio(1280,720).
                setCropShape(CropImageView.CropShape.RECTANGLE).start(getActivity());
          }
        });
    }

    /**
     * Listener to navigate to the previous fragment or the next one.
     * @param b button to listen
     * @param frag fragment to switch
     * @param TAG reference of the fragment
     */
    private void navigationListener (Button b, final Fragment frag, final String TAG) {
        b.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String m = String.valueOf(month);
              String d = String.valueOf(day);
              String minute = String.valueOf(min);

              if (day < 10) d = "0"+day;
              if (month < 10) m = "0"+month;
              if (min < 10) minute = "0"+min;

              String date_format = year+m+d;
              String date = day + "/" + month + "/" + year;
              String time = hour + ":" + minute;

              act.addItem("date",date);
              act.addItem("time",time);
              act.addItem("date_format",date_format);
              act.addItem("name", name.getText().toString());
              act.addItem("location", location.getText().toString());
              act.addItem("description", description.getText().toString());

              act.updateFragment(frag,TAG);
          }
        });
    }

    /**
     * Displays the image uploaded by the user.
     */
    public void setEventImage () {
        Uri uri = act.getmUri();

        if (uri != null) {
          eventImage.setVisibility(View.VISIBLE);
          Glide.with(eventImage.getContext()).load(new File(uri.getPath())).into(eventImage);
        }
    }

    /**
     * date_time text view listener that opens a date and time dialog.
     */
    private void dateTimeListener() {
        setDate();

        date_time.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            DatePickerDialog dialogDate = new DatePickerDialog(
                    getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,
                    mDateSetListener, year, month, day);

            Objects.requireNonNull(dialogDate.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            dialogDate.show();
          }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int month, int day) {
                setDay(day);
                setMonth(month);
                setYear(year);
                setDate();

                TimePickerDialog dialogTime = new TimePickerDialog(
                    getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,
                    mTimeSetListener,hour,min,true);

                Objects.requireNonNull(dialogTime.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

                dialogTime.show();
          }};

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setHour(hourOfDay);
            setMin(minute);
            setDate();
          }
        };
    }

    /**
     * Display the date in the date_time Text View.
     */
    public void setDate() {
        String minute = String.valueOf(this.min);

        if (this.min == 0) minute = "00";
        else if (this.min < 10) minute = "0" + this.min;

        String date = this.day + " " + getMonth(this.month) + ", at " + this.hour + ":" + minute;
        date_time.setText(date);
    }

    /**
    * Update data when the image is done uploading.
    */
    public void setInvisible() {
        uploading = false;
        progress.setVisibility(View.INVISIBLE);
        upload.setVisibility(View.VISIBLE);
    }


    /**
    * Update data when the image is uploading.
    */
    public void setVisible() {
        uploading = true;
        progress.setVisibility(View.VISIBLE);
        upload.setVisibility(View.INVISIBLE);
    }

    /**
    * Converts a month in int form to a month in string form.
    * @param month index
    * @return corresponding string
    */
    private String getMonth(int month) {
    return new DateFormatSymbols().getMonths()[month];
    }

    /**
     * Set the hour from the dialog.
     * @param hour to be set
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Set the minutes from the dialog.
     * @param min to be set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Set the day from the dialog.
     * @param day to be set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Set the month from the dialog.
     * @param month to be set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Set the year from the dialog.
     * @param year to be set
     */
    public void setYear(int year) {
        this.year = year;
    }
}
