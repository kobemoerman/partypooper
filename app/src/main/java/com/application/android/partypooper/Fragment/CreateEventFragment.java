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
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.android.partypooper.Activity.RegisterActivity;
import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class CreateEventFragment extends Fragment implements Events {

  private Button next, back;
  private TextView date_time;
  private EditText name, location, description;

  private Calendar cal;
  private int year, month, day, hour, min;
  private DatePickerDialog.OnDateSetListener mDateSetListener;
  private TimePickerDialog.OnTimeSetListener mTimeSetListener;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_create_event, container, false);

    findFragmentElements(view);

    navigationListener();

    dateListener();

    return view;
  }

  private void dateListener() {
    cal = Calendar.getInstance();

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);

    hour = cal.get(Calendar.HOUR)+1;
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

    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO: send the user to next fragment.
      }
    });

    back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new EventSelectionFragment());
      }
    });
  }

  private void openCreateEventFragment(Fragment frag) {
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
  }

  private void findFragmentElements(View view) {
    next = view.findViewById(R.id.frag_event_next);
    back = view.findViewById(R.id.frag_event_back);
    date_time = view.findViewById(R.id.frag_event_date_time);
    name = view.findViewById(R.id.frag_event_name);
    location = view.findViewById(R.id.frag_event_location);
    description = view.findViewById(R.id.frag_event_description);
  }
}
