package com.application.android.partypooper.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class CreateEventFragment extends Fragment implements Events {

  private Boolean picInPlace;

  private Button mNext, mBack;
  private RelativeLayout imageContainer;
  private ImageView tempImage, eventImage;
  private TextView date_time, mImage;
  private EditText mName, mLocation, mDescription;

  private int year, month, day, hour, min;
  private DatePickerDialog.OnDateSetListener mDateSetListener;
  private TimePickerDialog.OnTimeSetListener mTimeSetListener;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_create_event, container, false);

    picInPlace = false;

    findFragmentElements(view);

    eventImage.setVisibility(View.INVISIBLE);

    navigationListener();

    dateTimeListener();

    uploadImageListener();

    setImageListener();

    return view;
  }

  private void setImageListener() {
    FirebaseUser mUser = ((CreateEventActivity)getActivity()).getmUser();
    String timeStamp = ((CreateEventActivity)getActivity()).getTimeStamp();

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events").
            child(timeStamp + "?" + mUser.getUid());

    ref.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Event event = dataSnapshot.getValue(Event.class);

        if (event.getImageURL() != null && !picInPlace) {
          tempImage.setVisibility(View.INVISIBLE);
          eventImage.setVisibility(View.VISIBLE);
          Glide.with(eventImage.getContext()).load(event.getImageURL()).into(eventImage);
          picInPlace = true;
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void uploadImageListener() {
    imageContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CropImage.activity().setAspectRatio(1280,720).
                setCropShape(CropImageView.CropShape.RECTANGLE).start(getActivity());
      }
    });
    mImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CropImage.activity().setAspectRatio(1280,720).
                setCropShape(CropImageView.CropShape.RECTANGLE).start(getActivity());
      }
    });
  }

  private void dateTimeListener() {
    Calendar cal = Calendar.getInstance();

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);

    hour = cal.get(Calendar.HOUR_OF_DAY)+1;
    min = 0;

    String date = day + " " + getMonth(month) + ", at " + hour + ":00";
    date_time.setText(date);

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
        String date = day + " " + getMonth(month) + ", at " + hour + ":00";
        date_time.setText(date);

        TimePickerDialog dialogTime = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT,
                mTimeSetListener,hour,min,true);

        Objects.requireNonNull(dialogTime.getWindow()).setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        dialogTime.show();
      }};

    mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min = String.valueOf(minute);
        if (minute < 10) min = "0" + min;

        String date = day + " " + getMonth(month) + ", at " + hourOfDay + ":" + min;
        System.out.println(date);
        date_time.setText(date);
      }
    };
  }

  private String getMonth(int month) {
    return new DateFormatSymbols().getMonths()[month-1];
  }

  private void navigationListener () {
    mNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String date = day + "/" + month + "/" + year;
        String time = hour + ":" + min;

        saveEventInformation(date,time);

        openInviteFriendsFragment(new InviteFriendsFragment());
      }
    });

    mBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new EventThemeFragment());
      }
    });
  }

  private void openInviteFriendsFragment(Fragment frag) {
    String TAG = "InviteFriendsFragment";
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag, TAG).commit();
  }

  private void saveEventInformation(String date, String time) {
    ((CreateEventActivity)getActivity()).addValueDatabase("date",date);
    ((CreateEventActivity)getActivity()).addValueDatabase("time",time);

    ((CreateEventActivity)getActivity()).addValueDatabase("name",
            mName.getText().toString());
    ((CreateEventActivity)getActivity()).addValueDatabase("location",
            mLocation.getText().toString());
    ((CreateEventActivity)getActivity()).addValueDatabase("description",
            mDescription.getText().toString());
  }

  private void openCreateEventFragment(Fragment frag) {
    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(
            R.id.event_fragment_container, frag).commit();
  }

  private void findFragmentElements(View view) {
    mNext = view.findViewById(R.id.frag_event_next);
    mBack = view.findViewById(R.id.frag_event_back);
    date_time = view.findViewById(R.id.frag_event_date_time);
    mName = view.findViewById(R.id.frag_event_name);
    mLocation = view.findViewById(R.id.frag_event_location);
    mDescription = view.findViewById(R.id.frag_event_description);
    mImage = view.findViewById(R.id.frag_event_upload_photo);
    eventImage = view.findViewById(R.id.frag_event_image2);
    tempImage = view.findViewById(R.id.frag_event_image1);
    imageContainer = view.findViewById(R.id.frag_event_image);
  }
}
