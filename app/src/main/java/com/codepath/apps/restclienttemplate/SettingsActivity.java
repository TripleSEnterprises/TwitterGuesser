package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivitySettingsBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = "SettingsActivity";
    ActivitySettingsBinding binding;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        user = ParseUser.getCurrentUser();

        binding.setUser(user);

        binding.tiEditUsername.setText(user.getUsername());

        binding.swEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.swEditUsername.isChecked()){
                    binding.tiEditUsername.setEnabled(true);
                }
                else{
                    binding.tiEditUsername.setEnabled(false);
                    binding.tiEditUsername.setText(user.getUsername());
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.swEditUsername.isChecked()){
                    user.setUsername(String.valueOf(binding.tiEditUsername.getText()));
                }
                if(binding.swFilter.isChecked()){
                    // Set filter to true
                }
                else{
                    // Set filter to false
                }
                if(binding.swEditPicture.isChecked()){
                    // Edit the picture
                }

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG,"User settings saved");
                        finish();
                    }
                });

            }
        });

    }
}