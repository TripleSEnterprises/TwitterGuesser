package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText tiUsername;
    Button btnRegister;
    SwitchMaterial swProfile;
    Button btnUpload;
    ImageView ivProfile;

    public static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tiUsername = findViewById(R.id.tiUsername);
        btnRegister = findViewById(R.id.btnRegister);
        swProfile = findViewById(R.id.swProfile);
        btnUpload = findViewById(R.id.btnUpload);
        ivProfile = findViewById(R.id.ivProfile);

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
            if(swProfile.isChecked()){
                useTwitterImage();
            }
            else{
                uploadImage();
            }
        });

        swProfile.setOnClickListener(v->{
            if(swProfile.isChecked()){
                btnUpload.setVisibility(View.GONE);
                ivProfile.setVisibility(View.GONE);
            }
            else{
                btnUpload.setVisibility(View.VISIBLE);
                ivProfile.setVisibility(View.VISIBLE);
            }
        });

    }

    private void goToMainActivity() {
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
    }

    private void uploadImage(){

    }
    private void useTwitterImage(){
        Log.i(TAG,"Getting user Image");
        try{
            JSONObject twitterUser = ParseUser.getCurrentUser().getJSONObject("authData").getJSONObject("twitter");
            String id = twitterUser.getString("id");
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG,"Failure fetching",e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String image="";
                    try {
                        JSONObject userJson = new JSONObject(response.body().string());
                        image = userJson.getString("profile_image_url_https");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String finalImage = image;
                    Log.i(TAG,image);
                    ParseUser.getCurrentUser().put("cachedPicture",image);
                    ParseUser.getCurrentUser().saveInBackground(v->{
                        Log.i(TAG,"Image saved "+ finalImage);
                    });
                }
            };
            TwitterClient.getUser(callback, id);
        } catch(Exception e){
            Log.e(TAG,"Error fetching user id",e);
        }
    }
}