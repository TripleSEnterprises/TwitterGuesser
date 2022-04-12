package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;
import com.parse.twitter.ParseTwitterUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;

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
				goToRegisterActivity();
			} else {
				Log.i(TAG, "User logged in through Twitter!");
				goToMainActivity();
			}
		}));

		//Comment/Uncomment this until logout is implemented for login flow
		if(ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated()){
			goToMainActivity();
		}

	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void goToMainActivity() {
		Toast.makeText(this,"User logged in",Toast.LENGTH_SHORT).show();
		try {
			JSONObject twitterUser = ParseUser.getCurrentUser().getJSONObject("authData").getJSONObject("twitter");
			Log.i(TAG, twitterUser.getString("screen_name") + " logged in");
			Log.i(TAG, "Twitter id: "+twitterUser.getString("id"));
		} catch (JSONException e){
			Log.e(TAG,"JSON exception on goToMainActivity",e);
		}
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	private void goToRegisterActivity(){
		Toast.makeText(this,"Registering user",Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,RegisterActivity.class);
		startActivity(i);
	}



}
