package com.application.android.partypooper;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
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
        registerButton = findViewById(R.id.registerButtonReg);
        usernameEditText = findViewById(R.id.usernameTextReg);
        birthdayTextView = findViewById(R.id.ageTextReg);
        emailEditText = findViewById(R.id.mailTextReg);
        passwordEditText = findViewById(R.id.keyTextReg);
        confirmationEditText = findViewById(R.id.confirmTextReg);
        progressBar = findViewById(R.id.progressBarReg);

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
                    showMessage("Enter Valid E-mail",true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showMessage("Enter Password",true);
                    return;
                }

                if (!password.equals(confirm) || TextUtils.isEmpty(confirm)) {
                    showMessage("Different Password Confirmation",true);
                    return;
                }
                createUserAccount(email,password);
            }
        });
    }

    private void createUserAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showMessage("Account Created",false);
                    updateUI();
                } else {
                    showMessage(task.getException().getMessage(),true);
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
        mDate = findViewById(R.id.ageTextReg);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = day + "/" + month+1 + "/" + year;
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
