package com.codepath.apps.restclienttemplate;

import android.app.Application;

import com.codepath.apps.restclienttemplate.models.Game;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.twitter.ParseTwitterUtils;

public class TwitterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Game.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.PARSE_APPLICATION_ID)
                .clientKey(BuildConfig.PARSE_CLIENT_ID)
                .server("https://parseapi.back4app.com")
                .build()
        );

        ParseTwitterUtils.initialize(BuildConfig.TWITTER_CONSUMER_KEY, BuildConfig.TWITTER_CONSUMER_SECRET);
    }
}