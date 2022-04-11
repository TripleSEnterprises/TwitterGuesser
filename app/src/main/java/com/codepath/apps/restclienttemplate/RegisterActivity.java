package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
//                uploadImage();
            }
            goToMainActivity();
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

    }

    private void goToMainActivity() {
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
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
        File image = new File(part_image);
        ParseFile parsefile = new ParseFile(image);
        parsefile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!= null){
                    Log.e(TAG,"File could not be saved",e);
                    return;
                }
                ParseUser.getCurrentUser().put("picture",image);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null)
                            Log.i(TAG,"Saved username and image");
                        else
                            Log.e(TAG,"Error saving user",e);
                    }
                });
            }
        });
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
}