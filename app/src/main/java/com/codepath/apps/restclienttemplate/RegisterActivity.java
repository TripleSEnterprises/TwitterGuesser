package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    Uri selectedImage;
    String part_image;
    JSONObject twitterUser;
    SwitchMaterial swUsername;
    String username;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        swUsername = findViewById(R.id.swUsername);

        try {
            twitterUser = ParseUser.getCurrentUser().getJSONObject("authData").getJSONObject("twitter");
            username = twitterUser.getString("screen_name");
            tiUsername.setText(username);

        } catch (JSONException e) {
            Log.e(TAG,"couldn't initialize user",e);
        }

        swUsername.setOnClickListener(v->{
            if(swUsername.isChecked()){
                tiUsername.setText(username);
                tiUsername.setEnabled(false);
            }
            else{
                tiUsername.setText("");
                tiUsername.setEnabled(true);
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

        btnUpload.setOnClickListener(v -> {
            getImage();
        });

        btnRegister.setOnClickListener(v-> {
            String username = tiUsername.getText().toString();
            ParseUser.getCurrentUser().setUsername(username);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null)
                        Log.i(TAG,"Saved username " + username);
                    else
                        Log.e(TAG,"Error saving username", e);
                }
            });
            if(swProfile.isChecked()){
                Log.i(TAG,"Using twitter image");
                useTwitterImage();
            }
            else{
                Log.i(TAG,"Using uploaded image at "+part_image);
                uploadImage();
            }
            goToMainActivity();
        });



    }

    private void goToMainActivity() {
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
        finish();
    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            selectedImage = data.getData();
            String[] imageProjection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,imageProjection,null,null,null);
            if(cursor != null){
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                part_image = cursor.getString(indexImage);
                Bitmap bitmap = null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                }catch(IOException e){
                    Log.e(TAG,"Error retrieving file",e);
                }
                Glide.with(this)
                        .load(bitmap)
                        .circleCrop()
                        .into(ivProfile);
            }
        }

    }

    private void uploadImage(){
        verifyStoragePermissions(this);
        Log.i(TAG,"verified");
        File image = new File(part_image);
        Log.i(TAG, String.valueOf(image.canRead()));
        ParseFile parsefile = new ParseFile(image);
        parsefile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!= null){
                    Log.e(TAG,"File could not be saved",e);
                    return;
                }
                ParseUser.getCurrentUser().put("picture",parsefile);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null)
                            Log.i(TAG,"Saved file");
                        else
                            Log.e(TAG,"Error saving File",e);
                    }
                });
            }
        });
    }

    private void useTwitterImage(){
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
                    ParseUser.getCurrentUser().put("cachedPicture",finalImage);
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}