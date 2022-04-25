package com.codepath.apps.restclienttemplate.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserDataConflictResolver {
    private static final String TAG = "UserDataConflict";

    public static void setImageViewWithURLOnMainThread(final ImageView imageView, final String imageURL) {
        ContextCompat.getMainExecutor(imageView.getContext()).execute(new Runnable() {
            @Override
            public void run() {
                Glide
                    .with(imageView)
                    .load(imageURL)
                    .fallback(R.drawable.profile_icon)
                    .into(imageView);
            }
        });
    }


    private static void fallbackProfileImage(final ImageView imageView) {
        setImageViewWithURLOnMainThread(imageView, null);
    }

    public static void profileImageResolver(final ImageView imageView, ParseUser user) {
        /* Order of resolution:
        * 1. Profile Picture
        * 2. If Not Current User, cachedPicture
        * 3. If Current User, Get Direction From Twitter
        *    -> Set Cached Picture */

        // Circle Crop will be handled outside of this function.

        ParseFile profilePicture = user.getParseFile("picture");
        if (profilePicture != null) {
            profilePicture.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e != null) return;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
        else if (ParseUser.getCurrentUser() != user) {
            setImageViewWithURLOnMainThread(imageView, user.getString("cachedPicture"));
        }
        else if (ParseUser.getCurrentUser() == user) {
            try {
                String twitterId = Objects.requireNonNull(user
                        .getJSONObject("authData"))
                        .getJSONObject("twitter")
                        .getString("id");

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG,"Failure fetching Profile Picture", e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            String image = new JSONObject(
                                    Objects.requireNonNull(response.body()).string()
                            ).getString("profile_image_url_https");
                            image = image.replace("normal", "bigger");
                            setImageViewWithURLOnMainThread(imageView, image);

                            // This task is allowed to fail
                            user.put("cachedPicture", image);
                            user.saveEventually();
                        } catch (JSONException | NullPointerException e) {
                            Log.e(TAG,"Couldn't retrieve response image", e);
                        }
                    }
                };
                TwitterClient.getUser(callback, twitterId);
            } catch (JSONException | NullPointerException e) {
                fallbackProfileImage(imageView);
            }
        }
        else {
            fallbackProfileImage(imageView);
        }
    }
}
