package com.application.android.partypooper.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.android.partypooper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usernameEditText;
    private TextView birthdayTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmationEditText;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserID;

    private TextView mDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerClickListener();

        birthdayClickListener();
    }

    private void registerClickListener() {
        registerButton = findViewById(R.id.register_register_button);
        usernameEditText = findViewById(R.id.register_username);
        birthdayTextView = findViewById(R.id.register_age);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        confirmationEditText = findViewById(R.id.register_password_confirm);
        progressBar = findViewById(R.id.register_progress_bar);

        progressBar.setVisibility(View.INVISIBLE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                mAuth = FirebaseAuth.getInstance();

                String username = usernameEditText.getText().toString();
                String age = birthdayTextView.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmationEditText.getText().toString();

                if (!isEmailValid(email)){
                    showMessage("Enter valid e-mail",true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showMessage("Enter password",true);
                    return;
                }

                if (!password.equals(confirm) || TextUtils.isEmpty(confirm)) {
                    showMessage("Passwords don't match",true);
                    return;
                }
                createUserAccount(email,password,username,age);
            }
        });
    }

    private void saveUserInformation(String username, String age) {
        final String status = "Hi, I'm using PartyPooper!";

        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        HashMap userMap = new HashMap();
        userMap.put("id",currentUserID);
        userMap.put("username",username);
        userMap.put("status",status);
        userMap.put("gender","");
        userMap.put("age",age);

        userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    showMessage("Account Created",false);
                    updateUI();
                } else {
                    showMessage(Objects.requireNonNull(task.getException()).getMessage(),true);
                }
            }
        });
    }

    private void createUserAccount(String email, String password, final String username, final String age) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserInformation(username,age);
                } else {
                    showMessage(Objects.requireNonNull(task.getException()).getMessage(),true);
                }
            }
        });

    }

    private void updateUI() {
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void birthdayClickListener() {
        mDate = findViewById(R.id.register_age);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

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
                String date = day + "/" + month + "/" + year;
                mDate.setText(date);
                mDate.setTextColor(Color.BLACK);
            }
        };
    }

    private void showMessage(String s, boolean failed) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
        if (failed) {
            registerButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
