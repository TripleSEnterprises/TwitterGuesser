package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class TweetUser {

    String name;
    String screenName;
    String publicImageUrl;

    public TweetUser() {}

    public static TweetUser fromJson(JSONObject jsonObject) throws JSONException {
        TweetUser tweetUser = new TweetUser();
        tweetUser.name = jsonObject.getString("name");
        tweetUser.screenName = jsonObject.getString("screen_name");
        tweetUser.publicImageUrl = jsonObject.getString("profile_image_url_https");
        return tweetUser;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getPublicImageUrl() {
        return publicImageUrl;
    }
}

