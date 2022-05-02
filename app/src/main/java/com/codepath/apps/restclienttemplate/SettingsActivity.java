package com.codepath.apps.restclienttemplate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivitySettingsBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = "SettingsActivity";
    ActivitySettingsBinding binding;
    ParseUser user;
    Uri selectedImage;
    String part_image;
    Bitmap bmp;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private JSONObject twitterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        user = ParseUser.getCurrentUser();

        binding.tiEditUsername.setText(user.getUsername());

        binding.swFilter.setChecked(user.getBoolean("profanityFilter"));

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
                    user.put("profanityFilter",true);
                }
                else{
                    user.put("profanityFilter",false);
                }
                if(binding.swEditPicture.isChecked()) {
                    uploadImage();
                }
                else {
                    if(binding.swTwitterProfile.isChecked()) {
                        user.remove("picture");
                        if(user.get("cachedPicture") == null || user.get("cachedPicture") == ""){
                            useTwitterImage(user);
                        }
                    }
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i(TAG,"User settings saved");
                            finish();
                        }
                    });
                }

            }
        });

        binding.swEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.swEditPicture.isChecked()){
                    binding.btnEditUpload.setVisibility(View.VISIBLE);
                    binding.ivEditPicture.setVisibility(View.VISIBLE);
                    binding.swTwitterProfile.setVisibility(View.GONE);
                }
                else{
                    binding.btnEditUpload.setVisibility(View.GONE);
                    binding.ivEditPicture.setVisibility(View.GONE);
                    binding.swTwitterProfile.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.btnEditUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        try {
            twitterUser = ParseUser.getCurrentUser().getJSONObject("authData").getJSONObject("twitter");
        } catch (JSONException e) {
            Log.e(TAG,"couldn't initialize user",e);
        }

    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            selectedImage = data.getData();
            String[] imageProjection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,imageProjection,null,null,null);
            if(cursor != null){
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                part_image = cursor.getString(indexImage);
                cursor.close();
                try{
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                }catch(IOException e){
                    Log.e(TAG,"Error retrieving file",e);
                }
                Glide.with(this)
                        .load(bmp)
                        .circleCrop()
                        .into(binding.ivEditPicture);

            }
        }
    }

    private void uploadImage() {
        verifyStoragePermissions(this);
        Log.i(TAG, "verified");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        ParseFile parsefile = new ParseFile("uploaded_pfp.jpg", stream.toByteArray());
        parsefile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "File could not be saved", e);
                    return;
                }
                ParseUser.getCurrentUser().put("picture", parsefile);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i(TAG, "Saved file");
                            finish();
                        }
                        else {
                            Log.e(TAG, "Error saving File", e);
                        }
                    }
                });
            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        Log.d(TAG, String.format("permission != PackageManager.PERMISSION_GRANTED: %b", (permission != PackageManager.PERMISSION_GRANTED)));
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void useTwitterImage(ParseUser user){
        Log.i(TAG,"Getting user Image");
        try{
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
                        Log.e(TAG,"Couldn't retrieve response image",e);
                    }
                    String finalImage = image;
                    Log.i(TAG,finalImage);
                    user.put("cachedPicture",finalImage);
                    user.saveInBackground(v->{
                        Log.i(TAG,"User saved");
                        finish();
                    });
                }
            };
            TwitterClient.getUser(callback, id);
        } catch(Exception e){
            Log.e(TAG,"Error fetching user id",e);
        }
    }
}