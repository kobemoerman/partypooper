package com.application.android.partypooper.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    /** View button to register */
    private Button registerButton;

    /** View text view updated by DatePicker */
    private TextView userDate;

    /** Logo to be displayed on the screen */
    private ImageView image;

    /** View progress bar after pressing the button */
    private ProgressBar progressBar;

    /** View edit text items */
    private EditText userUsername, userPassword, passwordConfirmation, userMail;

    /** Calendar instance */
    private Calendar cal;

    /** Current date */
    private int year, month, day;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Dialog to pick a date */
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        onClickRegisterBirthday();
        hideKeyboardListener(userMail);
        hideKeyboardListener(userUsername);
        hideKeyboardListener(userPassword);
        hideKeyboardListener(passwordConfirmation);
    }

    /**
     * Initialises the view of the activity and Firebase.
     */
    private void initView() {
        mAuth = FirebaseAuth.getInstance();

        image = findViewById(R.id.register_logo);
        registerButton = findViewById(R.id.register_register_button);
        userUsername = findViewById(R.id.register_username);
        userDate = findViewById(R.id.register_age);
        userMail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        passwordConfirmation = findViewById(R.id.register_confirmation);
        progressBar = findViewById(R.id.register_progress_bar);

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        progressBar.setVisibility(View.INVISIBLE);

        Glide.with(getApplicationContext()).load(R.drawable.logo).into(image);
    }

    /**
     * Authenticate a new user with a given email and password.
     *
     * @param email    used to sign in.
     * @param password used to sign in.
     * @param username the user will be displayed by.
     * @param age      the user is.
     */
    private void createUserAccount(String email, String password, final String username, final String age) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) saveUserInformation(username, age);

                else showMessage(Objects.requireNonNull(task.getException()).getMessage(), true);
            }
        });
    }

    /**
     * Create a new node in the Users root.
     *
     * @param username to be saved.
     * @param age      to be saved.
     */
    private void saveUserInformation(String username, String age) {
        final String status = "Hi, I'm using PartyPooper!";
        final String gender = "";

        String mUser = mAuth.getCurrentUser().getUid();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser);

        HashMap userMap = new HashMap();
        userMap.put("id", mUser);
        userMap.put("username", username);
        userMap.put("status", status);
        userMap.put("gender", gender);
        userMap.put("age", age);

        mRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) updateUI();

                else showMessage(Objects.requireNonNull(task.getException()).getMessage(), true);
            }
        });
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
     * On click listener for the sign up button.
     * Launches the Home Activity if successful and kills the current one.
     *
     * @param view view of the activity
     */
    public void onClickRegisterSignUp(View view) {
        registerButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String username = userUsername.getText().toString();
        if (username.isEmpty()) {
            showMessage("Enter username", true);
            return;
        }

        String age = userDate.getText().toString();
        if (!checkAge(age)) {
            showMessage("Enter valid age", true);
            return;
        }

        String email = userMail.getText().toString();
        if (!checkEmail(email)) {
            showMessage("Enter valid e-mail", true);
            return;
        }

        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showMessage("Enter password", true);
            return;
        }

        String confirm = passwordConfirmation.getText().toString();
        if (!password.equals(confirm) || TextUtils.isEmpty(confirm)) {
            showMessage("Passwords don't match", true);
            return;
        }

        createUserAccount(email, password, username, age);
    }

    /**
     * On click listener for the birth date text view.
     * Opens a DatePicker dialog.
     */
    private void onClickRegisterBirthday() {
        userDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        mDateSetListener, year, month, day);

                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;

                String d = String.valueOf(day);
                String m = String.valueOf(month);

                if (day < 10) d = "0"+day;
                if (month < 10) m = "0"+month;

                String date = d + "/" + m + "/" + year;
                userDate.setText(date);
                userDate.setTextColor(Color.BLACK);
            }
        };
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
     * Launches the Home Activity and kills the current one.
     */
    private void updateUI() {
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    /**
     * Checks if the input email is valid.
     * @param email string from the edit text
     * @return false if email is valid
     */
    public static boolean checkEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean checkAge(String age) {
        int d = Integer.parseInt(age.substring(0,2));
        int m = Integer.parseInt(age.substring(3,5));
        int y = Integer.parseInt(age.substring(6,10));

        System.out.println(d + ":" + day + " " + m + ":" + (month+1) + " " + y + ":" + year);

        if (y > year) return false;

        if (y == year && m > (month+1)) return false;

        if (y == year && m == (month+1) && d > day) return false;

        return true;
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     * @param failed resets progress bar
     */
    private void showMessage(String s, boolean failed) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
        if (failed) {
            registerButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}