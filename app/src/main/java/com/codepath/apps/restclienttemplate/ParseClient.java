package com.codepath.apps.restclienttemplate;

import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Game;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.annotation.Nullable;

public class ParseClient {
    private static final String TAG = "ParseClient";
    public static final String GAME_JSON_ARRAY_KEY = "question";

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

    public static void getTopPlayers(int after,FindCallback<ParseUser> topPlayersCallback){
        ParseQuery<ParseUser> query= ParseQuery.getQuery(ParseUser.class)
                .setLimit(100)
                .setSkip(after)
                .addDescendingOrder("highScore");
        query.findInBackground(topPlayersCallback);
    }

    public static void updateUserHighScore(ParseUser user,
                                           Number finalScore,
                                           SaveCallback updateHighScoreCallback) {
        user.put("highScore", finalScore);
        user.saveInBackground(updateHighScoreCallback);
    }

    public static void insertGameResult(JSONArray gameHistory,
                                        Number finalScore,
                                        SaveCallback gameResultInsertCallback) {
        Game game = new Game();
        try {
            game.setQuestions((new JSONObject()).put(GAME_JSON_ARRAY_KEY, gameHistory));
        } catch (JSONException ignored) {
        }
        game.setFinalScore(finalScore);
        game.setUser(ParseUser.getCurrentUser());
        game.saveInBackground(gameResultInsertCallback);
    }
}
