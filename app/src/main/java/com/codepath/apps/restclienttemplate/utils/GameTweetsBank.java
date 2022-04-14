package com.codepath.apps.restclienttemplate.utils;

import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class GameTweetsBank {

    public static final String TAG = "GameTweetsBank";

    // Amount of tweets to be randomly selected from tweets object
    private static final int TWEETS_TOTAL_PICK_MIN = 30;
    private static final int TWEETS_TOTAL_PICK_MAX = 50;
    private static final int TWEETS_FRIEND_PICK_MAX = 10;
    private static final int FRIENDS_PICK_MAX = 5;

    // Stores history of used Tweets
    private Set<String> usedTweetIds;

    // Game Question History
    private JSONArray gameQuestionHistory;

    // Question Bank
    private List<Tweet> gameQuestionBank;

    // Question
    private JSONObject gameQuestion;

    public GameTweetsBank() {
        /* Get Friends
            * Fetch from endpoint
            * Parse out ids and store in String[]
         */

        /* Select random number of friends ( (String[] of twitter_uids)
            * For each friend get (10) tweets via endpoint (tweet-extended)
                * Parse User and status fields
                * Create gameQuestion object
                * Add to question bank
         */
        try {
            fillQuestionBank();
        } catch (JSONException e) {
            Log.e(TAG, "Error handling JSON", e);
        }
    }

    /**
     * Returns a list of friend ids given a JSONArray of friend_ids returned by Twitter API
     * @param friend_ids JSONArray
     * @return List of Twitter friend ids
     */
    private static List<String> getFriendsList(JSONArray friend_ids) {
        // TODO
        // Fetch from endpoint
        // Add friends to list
        // Return list
    }

    /**
     * Adds a tweet to the question bank using JSON.simple CRUD
     * @param randomTweet Tweet
     */
    private static void appendTweetToQuestionBank(Tweet randomTweet) {
        // TODO
    }

    /**
     * Checks Set for the tweet provided and validates if it was used or not
     * @param tweet Tweet
     * @return if tweet was used
     */
    private static boolean tweetUsed(Tweet tweet) {
    }

    /**
     * Fills question bank
     */
    private static void fillQuestionBank() throws JSONException {
        // TODO

    }

    /**
     * Adds a tweet to the game question history using JSON.simple CRUD
     * @param randomTweet Tweet
     */
    private static void appendTweetToQuestionHistory(Tweet randomTweet) {
        // TODO
    }

    /**
     * Returns a pseudo-randomly chosen tweet
     * @return a tweet object
     */
    public Tweet getTweet() {
        // TODO
        // if question bank empty, refresh (fillQuestionBank())
        // else pick pseudo-randomly from questionBank
        // Add question to questionHistory
    }

    /**
     * Gets tweets associated with the score a user earned from answering, used throughout the duration of the game
     * @return JSONArray of JSONObjects of schema: [{tweet: Tweet, score: Number}, ...]
     */
    public JSONArray getUsedTweets() {
        // TODO
    }

    /**
     * Given a tweet_id, finds the associated question in the question history and sets its score
     * @param tweet_id String
     * @param score Number
     */
    public void addScore(String tweet_id, Number score) {
        // TODO
        // JSONObject question = getUsedTweets().find(key where key.id.equals(tweet_id)
        // question.score = score
    }
}
