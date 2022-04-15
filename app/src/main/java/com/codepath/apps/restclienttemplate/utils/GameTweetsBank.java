package com.codepath.apps.restclienttemplate.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameTweetsBank {

    public static final String TAG = "GameTweetsBank";

    // Amount of tweets to be randomly selected from tweets object
    private static final int TWEETS_TOTAL_PICK_MIN = 30;
    private static final int TWEETS_TOTAL_PICK_MAX = 50;
    private static final int TWEETS_FRIEND_PICK_MAX = 10;
    private static final int FRIENDS_PICK_MAX = 5;

    // Game Question History
    private JSONArray gameQuestionHistory;

    // Question Bank
    private List<Tweet> gameQuestionBank;

    // Question
    private JSONObject gameQuestion;

    // Used Tweet Id Set
    private Set<String> usedTweetSet;

    // Map of Friends to a list of Tweets
    private Map<String, List<Tweet>> friend_tweet_map;
    private List<String> friend_ids;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GameTweetsBank() {
        this.friend_tweet_map = new HashMap<>();
        try {
            fillQuestionBank();
        } catch (JSONException e) {
            Log.e(TAG, "Error handling JSON", e);
        }
    }

    /**
     * Returns a list of friend ids given a JSONArray of friend_ids returned by Twitter API
     * @return List of Twitter friend ids
     */
    private static List<String> getFriendsIds() {
        // TODO FETCH FRIENDS
        // Fetch from endpoint (if friend hashmap isEmpty, return hashmap keys)
        // Add friends to hashmap
        // Return list
    }

    private static String[] getRandomFriends(List<String> friend_ids) {
        String[] randomlyChosenFriends = new String[FRIENDS_PICK_MAX];

        if(friend_ids.size() == 0) {
            Log.d(TAG, "No friends :(");
            // TODO ADD NO FRIEND DEFAULT LOSER LOGIC
        }

        Random random = new Random();
        for (int i = 0; i < FRIENDS_PICK_MAX; i++) randomlyChosenFriends[i] =
                                                    friend_ids.get(random.nextInt(friend_ids.size()));

        return randomlyChosenFriends;
    }

    /**
     * Fills question bank
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillQuestionBank() throws JSONException {
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
        do {
            String[] randomlyChosenFriends = getRandomFriends(!this.friend_ids.isEmpty() ? this.friend_ids : getFriendsIds());
            Set<String> usedTweetIds;
            List<Tweet> userTimeline;

            // Loop through each friend id from randomly generated array
            for (String friendId : randomlyChosenFriends) {
                // Stores culled Tweets for a single friend
                usedTweetIds = new HashSet<>();
                // Get list of tweets, either from stored HashMap or fetch via twitter endpoint
                userTimeline = this.friend_tweet_map.getOrDefault(friendId, fetchUserTimeline(friendId));
                // Map timeline to user for future use if they are not in the hash map
                if (!this.friend_tweet_map.containsKey(friendId))
                    this.friend_tweet_map.put(friendId, userTimeline);

                // Check if there are any tweets made by this user, if not skip them and remove from map
                if (userTimeline.size() == 0) {
                    this.friend_tweet_map.remove(friendId);
                    continue;
                }

                Random random = new Random();
                // Get random tweets from friend
                for (int i = 0; i < TWEETS_FRIEND_PICK_MAX; ) {
                    // if their tweets have been exhausted, exit the loop and go to the next friend
                    if (userTimeline.size() == 0) break;
                    int rand_idx = random.nextInt(userTimeline.size());
                    Tweet tweet = userTimeline.get(rand_idx);
                    String tweet_id = tweet.getId();
                    // Check if we have already processed this tweet against HashSet
                    // of tweet_ids to prevent duplicates
                    if (!this.usedTweetSet.contains(tweet_id) && !usedTweetIds.contains(tweet_id)) {
                        // add to global set
                        usedTweetIds.add(tweet_id);
                        userTimeline.remove(rand_idx);
                        this.gameQuestionBank.add(tweet);
                        i++;
                    }
                    if (this.gameQuestionBank.size() == TWEETS_TOTAL_PICK_MAX) return;
                }
                // Union local tweet set to global tweet set
                this.usedTweetSet.addAll(usedTweetIds);
            }
        } while(this.gameQuestionBank.size() >= TWEETS_TOTAL_PICK_MIN);
    }

    private List<Tweet> fetchUserTimeline(String friendId) {
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
