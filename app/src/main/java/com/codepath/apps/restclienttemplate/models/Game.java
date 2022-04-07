package com.codepath.apps.restclienttemplate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

@ParseClassName("Game")
public class Game extends ParseObject {

    public static final String KEY_QUESTIONS = "questions";
    public static final String KEY_FINAL_SCORE = "finalScore";
    public static final String KEY_USER = "user";

    public JSONObject getQuestions() {
        return getJSONObject(KEY_QUESTIONS);
    }

    public void setQuestions(JSONObject questions) {
        put(KEY_QUESTIONS, questions);
    }

    public Number getFinalScore() {
        return getNumber(KEY_FINAL_SCORE);
    }

    public void setFinalScore(Number finalScore) {
        put(KEY_FINAL_SCORE, finalScore);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
