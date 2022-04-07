package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.twitter.ParseTwitterUtils;

public class LoginActivity extends AppCompatActivity {

	Button login;
	private final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		login = findViewById(R.id.btnLogin);

		login.setOnClickListener(v-> ParseTwitterUtils.logIn(this, (user, err) -> {
			if (user == null) {
				Log.i(TAG, "Uh oh. The user cancelled the Twitter login.");
			} else if (user.isNew()) {
				Log.i(TAG, "User signed up and logged in through Twitter!");
				goToMainActivity();
			} else {
				Log.i(TAG, "User logged in through Twitter!");
				goToMainActivity();
			}
		}));

	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void goToMainActivity(){
//		Intent i = new Intent(this,MainActivity.class);
//		startActivity(i);
	}



}
