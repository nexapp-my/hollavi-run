package com.groomify.hollavirun;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class OnboardingActivity extends AppCompatActivity {

    private final static String TAG = OnboardingActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();



        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Signup button on click");
                launchSignUpScreen();
            }
        });

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Login button on click");
                launchWelcomeScreen();
            }
        });

    }

    private void launchSignUpScreen(){
        Intent intent = new Intent(OnboardingActivity.this, SignUpActivity.class);
        startActivity(intent);
        //finish();
    }

    private void launchWelcomeScreen(){
        Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
