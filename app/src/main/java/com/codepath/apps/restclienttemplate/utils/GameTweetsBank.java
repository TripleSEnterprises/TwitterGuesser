package com.codepath.apps.restclienttemplate.utils;

import android.util.Log;

import androidx.core.util.Pair;

import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import okhttp3.Response;

public class GameTweetsBank {

    public static final String TAG = "GameTweetsBank";

    // Amount of tweets to be randomly selected from tweets object
    public static final int TOTAL_TWEETS_PICK_MIN = 10;
    public static final int TOTAL_TWEETS_PICK_MAX = 20;
    public static final int FRIEND_TWEETS_PICK_MAX = 10;
    public static final int FRIENDS_PICK_MAX = 10;

    // Game Question History
    private final List<JSONObject> gameQuestionHistory;

    // Question Bank
    private final List<Tweet> gameQuestionBank;

    // Used Tweet Id Set
    private final Set<String> usedTweetSet;

    private static JSONArray staticFriendObjectsArray = new JSONArray();

    // Map of Friends to a list of Tweets
    private final static Map<String, List<Tweet>> friend_tweet_map = new HashMap<>();
    private Map<String, String> friend_id_name_map;
    private final static List<String> test_friend_ids = new ArrayList<>();
    private final static List<String> friend_ids = new ArrayList<>();

    public GameTweetsBank() {
        this.gameQuestionHistory = new ArrayList<>();
        this.gameQuestionBank = new Stack<>();
        this.usedTweetSet = new HashSet<>();
        this.friend_id_name_map = new HashMap<>();
        try {
            getFriendsIds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fillFriendsIds() throws JSONException {
        // Add friends to hashmap
        for(int i = 0; i < staticFriendObjectsArray.length(); i++) {
            TweetUser friend = TweetUser.fromJson(staticFriendObjectsArray.getJSONObject(i));
            String friend_id = friend.getId();
            friend_tweet_map.put(friend_id, new ArrayList<>());
            this.friend_id_name_map.put(friend_id, friend.getScreenName());
            // TODO Add after testing
            friend_ids.add(friend_id);
        }
        fillQuestionBank();
    }

    /**
     * Returns a list of friend ids given a JSONArray of friend_ids returned by Twitter API
     * @return List of Twitter friend ids
     */
    private void getFriendsIds() throws IOException {
        // if (!friend_ids.isEmpty()) return friend_ids;

        // Fetch from endpoint (if friend List !isEmpty, return stored list)
        Response response = TwitterClient.getFriendsUserObjects();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            Log.i(TAG, "SUCCESS!");
            try {
                String responseData = response.body().string();
                JSONObject responseObject = new JSONObject(responseData);
                JSONArray friendObjectsArray = responseObject.getJSONArray("users");
                staticFriendObjectsArray = friendObjectsArray;
            } catch (JSONException ignored) {
            } finally {
                response.close();
            }
        }
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

    private void fillQuestionBankHelper(List<Tweet> userTimeline) {
        // Stores culled Tweets for a single friend
        Set<String> usedTweetIds = new HashSet<>();
        // Get list of tweets, either from stored HashMap or fetch via twitter endpoint

        Random random = new Random();
        // Get random tweets from friend
        for (int i = 0; i < FRIEND_TWEETS_PICK_MAX; ) {
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
            } else {
                userTimeline.remove(rand_idx);
            }
            if (this.gameQuestionBank.size() >= TOTAL_TWEETS_PICK_MAX) return;
        }
        // Union local tweet set to global tweet set
        this.usedTweetSet.addAll(usedTweetIds);
    }

    /**
     * Fills question bank
     */
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
            String[] randomlyChosenFriends = getRandomFriends(friend_ids);

            // Loop through each friend id from randomly generated array
            for (String friendId : randomlyChosenFriends) {
                // Stores culled Tweets for a single friend
                if (friend_tweet_map.get(friendId).isEmpty()) {
                    String contentAsString = TwitterClient.fetchUserTimeline(friendId);
                    JSONArray tweetObjectsArray = new JSONArray(contentAsString);
                    List<Tweet> userTimeline;
                    try {
                        if (tweetObjectsArray.length() > 0) {
                            userTimeline = new ArrayList<>();
                            for (int i = 0; i < tweetObjectsArray.length(); i++) {
                                userTimeline.add(Tweet.fromJson(tweetObjectsArray.getJSONObject(i)));
                            }

                            friend_tweet_map.put(friendId, userTimeline);
                            fillQuestionBankHelper(userTimeline);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    fillQuestionBankHelper(friend_tweet_map.get(friendId));
                }
            }
            return;
        } while(this.gameQuestionBank.size() < TOTAL_TWEETS_PICK_MIN);
    }

    /**
     * Returns a pseudo-randomly chosen tweet
     * @return a tweet object
     */
    private Tweet getTweet() {
        // if question bank empty, refresh (fillQuestionBank())
        // else pick pseudo-randomly from questionBank
        // Add question to questionHistory
        try {
            if (this.gameQuestionBank.isEmpty()) fillQuestionBank();
        } catch (JSONException ignored) {
        }

        Tweet tweet;
        Random random = new Random();
        int randIdx = random.nextInt(this.gameQuestionBank.size());
        tweet = this.gameQuestionBank.get(randIdx);
        this.gameQuestionBank.remove(randIdx);
        this.usedTweetSet.add(tweet.getId());

        JSONObject tweetObject = new JSONObject();
        try {
            tweetObject.put("tweet_id", tweet.getId())
                    .put("score", 0);
        } catch (JSONException ignored) {
        }

        this.gameQuestionHistory.add(tweetObject);
        return tweet;
    }

    /**
     * Generates a question
     * @return Pair object with a Tweet tweet and a String[3] of friend_ids of wrongAnswers
     */
    public Pair<Tweet, String[]> getQuestion() {
        Tweet tweet = getTweet();
        final List<String> userOptions = new ArrayList<>(3);
        final List<String> userOptionsIds = new ArrayList<>(3);

        Random random = new Random();
        String randId;

        // add correct answer to cache for future use
        if (friend_id_name_map.get(tweet.getUser().getId()) != null)
            friend_id_name_map.put(tweet.getUser().getId(), tweet.getUser().getScreenName());

        // Add 3 random incorrect answers
        for (int i = 0; i < 3; i++) {
            // select a random friend id not yet chosen and not the actual answer
            do randId = friend_ids.get(random.nextInt(friend_ids.size()));
            while (randId.equals(tweet.getUser().getId()) || userOptionsIds.contains(randId));

            if (friend_id_name_map.get(randId) != null) {
                userOptions.add(friend_id_name_map.get(randId));
                userOptionsIds.add(randId);
                continue;
            }
            userOptionsIds.add(randId);
        }
        return new Pair<>(tweet, userOptionsIds.toArray(new String[3]));
    }

    /**
     * Gets tweets associated with the score a user earned from answering, used throughout the duration of the game
     * @return JSONArray of JSONObjects of schema: [{tweet: Tweet, score: Number}, ...]
     */
    public JSONArray getUsedTweets() {
        return new JSONArray(this.gameQuestionHistory);
    }

    /**
     * Given a tweet_id, finds the associated question in the question history and sets its score
     * @param tweet_id String
     * @param score Number
     */
    public void addScore(String tweet_id, Number score) {
        // JSONObject question = getUsedTweets().find(key where key.id.equals(tweet_id)
        // question.score = score
        for(int i = 0; i < this.gameQuestionHistory.size(); i++) {
            try {
                if (this.gameQuestionHistory.get(i).getString("tweet_id").equals(tweet_id)) {
                    this.gameQuestionHistory.get(i).put("score", score);
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public Map<String, String> getFriendIdNameMap() {
        return this.friend_id_name_map;
    }
}
