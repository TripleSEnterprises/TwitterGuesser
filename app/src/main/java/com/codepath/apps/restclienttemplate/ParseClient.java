package com.codepath.apps.restclienttemplate;

import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Game;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ParseClient {
    private static final String TAG = "ParseClient";

    public static void getMatchHistory(ParseUser user, FindCallback<Game> gameFindCallback) {
        getMatchHistory(user, null, gameFindCallback);
    }

    public static void getMatchHistory(ParseUser user, @Nullable Date dateBefore, FindCallback<Game> gameFindCallback) {
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class)
                .setLimit(10)
                .addDescendingOrder(Game.KEY_CREATED_AT)
                .whereEqualTo(Game.KEY_USER, user);
        if (dateBefore != null) query.whereLessThan(Game.KEY_CREATED_AT , dateBefore);

        query.findInBackground(gameFindCallback);
    }
}
