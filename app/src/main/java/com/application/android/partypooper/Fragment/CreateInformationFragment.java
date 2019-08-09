package com.application.android.partypooper.Fragment;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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

public class CreateInformationFragment extends Fragment implements Events {

    /** TAG reference of this fragment */
    private final static String TAG = "fragment/CreateInformation";

    /** Estimate of party length */
    private final static int INTERVAL = 3;

    /** Reference to the CreateEvent Activity */
    private CreateEventActivity act;

    /** Informs the user the image is uploading to the database */
    private ProgressBar progress;

    /** Buttons to navigate back and forth between fragments */
    private Button mNext, mBack;

    /** Displays the image uploaded by the user */
    private ImageView eventImage;

    /** Text views with on click listeners */
    private TextView start_date, end_date, upload;

    /** Information to be changed by the user */
    private EditText name, description, lnumber, lstreet, lcity;

    /** Containers to update with data */
    private RelativeLayout uploadContainer;

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
        View view = inflater.inflate(R.layout.fragment_create_information, container, false);

        initView(view);
        updateData();
        navigationListener(mBack, new CreateThemeFragment(),"fragment/EventTheme", false);
        navigationListener(mNext, new CreateInviteFragment(),"fragment/EventInvite", true);

        uploadImageListener(uploadContainer);

        hideKeyboardListener(name);
        hideKeyboardListener(lnumber);
        hideKeyboardListener(lstreet);
        hideKeyboardListener(lcity);
        hideKeyboardListener(description);

        dateTimeListener();

        return view;
    }

    /**
     * Initialises the fragment view, date and time.
     * @param view fragment_create_information
     */
    private void initView(View view) {
        Calendar cal = Calendar.getInstance();

        act = (CreateEventActivity) getActivity();
        assert act != null;

        mNext = view.findViewById(R.id.frag_create_info_next);
        mBack = view.findViewById(R.id.frag_create_info_back);
        name = view.findViewById(R.id.frag_create_name);
        start_date = view.findViewById(R.id.frag_create_start_date_time);
        end_date = view.findViewById(R.id.frag_create_end_date_time);
        lnumber = view.findViewById(R.id.frag_create_location_number);
        lstreet = view.findViewById(R.id.frag_create_location_street);
        lcity = view.findViewById(R.id.frag_create_location_city);
        description = view.findViewById(R.id.frag_create_description_text);
        uploadContainer = view.findViewById(R.id.frag_create_upload_photo);
        upload = view.findViewById(R.id.frag_create_upload_text);
        eventImage = view.findViewById(R.id.frag_create_image);
        progress = view.findViewById(R.id.frag_create_upload_progress);

        progress.setVisibility(View.INVISIBLE);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = 0;
    }

    /**
     * Updates the data if the user has already added information.
     */
    private void updateData() {
        // event name
        String name = act.getItem("name");
        if (name != null) this.name.setText(name);

        // event location
        String number = act.getItem("number");
        String street = act.getItem("street");
        String city = act.getItem("city");
        if (street != null) this.lstreet.setText(street);
        if (number != null) this.lnumber.setText(number);
        if (city != null) this.lcity.setText(city);

        // event start date and time
        String start_time = act.getItem("start_time");
        String start_date = act.getItem("start_date");
        if (start_time != null && start_date != null) {
            setDate(Integer.parseInt(start_date.substring(0,2)),
                Integer.parseInt(start_date.substring(3,5)), Integer.parseInt(start_date.substring(6,10)),
                Integer.parseInt(start_time.substring(0,2)), Integer.parseInt(start_time.substring(3,5)), this.start_date);
        } else {
            setDate(day,month,year,hour,min,this.start_date);
        }

        // event end date and time
        String end_time = act.getItem("end_time");
        String end_date = act.getItem("end_date");
        if (end_time != null && end_date != null) {
            setDate(Integer.parseInt(end_date.substring(0,2)),
                Integer.parseInt(end_date.substring(3,5)), Integer.parseInt(end_date.substring(6,10)),
                Integer.parseInt(end_time.substring(0,2)), Integer.parseInt(end_time.substring(3,5)), this.end_date);
        } else {
            setDate(day,month,year,(hour+INTERVAL < 24 ? hour+INTERVAL : hour-(24-INTERVAL)),min,this.end_date);
        }

        // event description
        String description = act.getItem("description");
        if (description != null) this.description.setText(description);

        // event date stamp
        String date_stamp = act.getItem("date_stamp");
        if (date_stamp != null) {
            this.year = Integer.parseInt(date_stamp.substring(0, 4));
            this.month = Integer.parseInt(date_stamp.substring(4, 6));
            this.day = Integer.parseInt(date_stamp.substring(6, 8));
            this.hour = Integer.parseInt(date_stamp.substring(8, 10));
            this.min = Integer.parseInt(date_stamp.substring(10, 12));
        }

        // event image
        setEventImage();
    }

    /**
     * Listener to upload an event image.
     */
    private void uploadImageListener(RelativeLayout layout) {
        if (act.isUploading()) {
          act.showMessage("Image is uploading");
          return;
        }

        layout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            CropImage.activity().setAspectRatio(2200,1000).
                setCropShape(CropImageView.CropShape.RECTANGLE).start(getActivity());
          }
        });
    }

    /**
     * Listener to navigate to the previous fragment or the next one.
     * @param b button to listen
     * @param frag fragment to switch
     * @param TAG reference of the fragment
     * @param next true if 'next' button is pressed
     */
    private void navigationListener (Button b, final Fragment frag, final String TAG, final boolean next) {
        b.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (act.isUploading()) {
                  act.showMessage("Image is uploading");
                  return;
              }

              final String _name = name.getText().toString();
              final String _number = lnumber.getText().toString();
              final String _street = lstreet.getText().toString();
              final String _city = lcity.getText().toString();

              if (next) {
                  if (_name.isEmpty()) {
                      act.showMessage("Title is missing");
                      return;
                  }

                  if (!getDateOrder()) {
                      act.showMessage("End date is invalid");
                      return;
                  }

                  if (_number.isEmpty() || _street.isEmpty() || _city.isEmpty()) {
                      act.showMessage("Invalid address");
                      return;
                  }
              }

              act.addItem("name", _name);
              act.addItem("number", _number);
              act.addItem("street", _street);
              act.addItem("city", _city);
              act.addItem("description", description.getText().toString());

              act.updateFragment(frag,TAG);
          }
        });
    }

    private boolean getDateOrder() {
        String start_time = act.getItem("start_time");
        String start_date = act.getItem("start_date");

        String end_time = act.getItem("end_time");
        String end_date = act.getItem("end_date");

        // year
        if (Integer.parseInt(start_date.substring(6,10)) > Integer.parseInt(end_date.substring(6,10))) return false;

        // month
        if (Integer.parseInt(start_date.substring(3,5)) > Integer.parseInt(end_date.substring(3,5))) return false;

        // day
        if (Integer.parseInt(start_date.substring(0,2)) > Integer.parseInt(end_date.substring(0,2))) return false;

        if (Integer.parseInt(start_date.substring(0,2)) == Integer.parseInt(end_date.substring(0,2))
            && Integer.parseInt(start_time.substring(0,2)) > Integer.parseInt(end_time.substring(0,2))) return false;

        // minute
        if (Integer.parseInt(start_date.substring(0,2)) == Integer.parseInt(end_date.substring(0,2))
            && Integer.parseInt(start_time.substring(0,2)) == Integer.parseInt(end_time.substring(0,2))
            && Integer.parseInt(start_time.substring(3,5)) > Integer.parseInt(end_time.substring(3,5))) return false;

        return true;
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
     * start_date text view listener that opens a date and time dialog.
     */
    private void dateTimeListener() {
        final TextView[] type = new TextView[1];

        start_date.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            type[0] = start_date;

            DatePickerDialog dialogDate = new DatePickerDialog(
                    getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,
                    mDateSetListener, year, month, day);

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
              setDate(day,month,year,hour,min, type[0]);

              TimePickerDialog dialogTime = new TimePickerDialog(
                    getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,
                    mTimeSetListener,displayTime(hour,type[0]),min,true);

                Objects.requireNonNull(dialogTime.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

                dialogTime.show();
          }};

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setDate(day,month,year,hourOfDay,minute, type[0]);
          }
        };
    }

    private int displayTime(int hour, TextView type) {
        if (type == end_date) {
            if ((hour + INTERVAL <= 24)) {
                return hour + INTERVAL;
            } else {
                return hour - (24 - INTERVAL);
            }
        } else {
            return hour;
        }
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

        String date = day + "/" + month + "/" + year;
        String time = hour + ":" + minute;

        if (type.equals(start_date)) {
            String date_stamp = year+month+day+hour+minute;
            act.addItem("start_date",date);
            act.addItem("start_time",time);
            act.addItem("date_stamp",date_stamp);
            start_date.setText(getDate(day,m,minute,hour));
            end_date.setText(String.format("End: %s", getDate(day, m, minute, hour)));
        } else {
            act.addItem("end_date",date);
            act.addItem("end_time",time);
            end_date.setText(String.format("End: %s", getDate(day, m, minute, hour)));
        }
    }

    /**
     * Display the date in the start_date Text View.
     */
    public String getDate(String day, int month, String min, String hour) {
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
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
    * Update data when the image is done uploading.
    */
    public void setInvisible() {
        act.setUploading(false);
        progress.setVisibility(View.INVISIBLE);
        upload.setVisibility(View.VISIBLE);
    }


    /**
    * Update data when the image is uploading.
    */
    public void setVisible() {
        act.setUploading(true);
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
