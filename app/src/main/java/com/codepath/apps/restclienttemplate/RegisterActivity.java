package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText tiUsername;
    Button btnRegister;

    public static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tiUsername = findViewById(R.id.tiUsername);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v-> {
            String username = tiUsername.getText().toString();
            ParseUser.getCurrentUser().setUsername(username);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(TAG, "User Registered as "+username);
                    goToMainActivity();
                }
            });
        });


    }

    private void goToMainActivity() {
        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
    }
}