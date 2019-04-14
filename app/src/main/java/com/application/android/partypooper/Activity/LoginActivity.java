package com.application.android.partypooper.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.application.android.partypooper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    /** view edit text items */
    private EditText userMail, userPassword;

    /** view button to sign in*/
    private Button loginButton;

    /** view progress bar after pressing the button*/
    private ProgressBar progressBar;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase user */
    private FirebaseUser mUser;

    /**
     * Checks if device is already logged in.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();

        if (mUser != null) updateActivity();
    }

    /**
     * On create method of the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    /**
     * Initialises the view of the activity and Firebase.
     */
    private void initView() {
        mAuth = FirebaseAuth.getInstance();

        userMail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_login_button);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Signs in user and updates the UI accordingly
     * @param mail from the edit text
     * @param password from the edit text
     */
    private void signUserIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateActivity();
                } else {
                    showMessage(task.getException().getMessage(),true);
                }
            }
        });

    }

    /**
     * On click listener for the login button.
     * Launches the Home Activity if successful and kills the current one.
     * @param view view of the activity
     */
    public void onClickLoginLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        final String mail = userMail.getText().toString();
        final String password = userPassword.getText().toString();

        if (!checkEmail(mail)) {
            showMessage("Enter Valid E-mail",true);
            return;
        }

        if (password.isEmpty()) {
            showMessage("Enter Password",true);
            return;
        }
        signUserIn(mail,password);
    }

    /**
     * On click listener for the register text.
     * Launches the Register Activity but doesn't kill the current one.
     * @param view view of the activity
     */
    public void onClickLoginRegister(View view) {
        Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     * Launches the Home Activity and kills the current one.
     */
    private void updateActivity() {
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     * @param failed resets progress bar
     */
    private void showMessage(String s, boolean failed) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
        if (failed) {
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks if the input email is valid.
     * @param email string from the edit text
     * @return false if email is valid
     */
    public static boolean checkEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
