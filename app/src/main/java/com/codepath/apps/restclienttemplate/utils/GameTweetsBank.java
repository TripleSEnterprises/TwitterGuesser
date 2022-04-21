package com.codepath.apps.restclienttemplate.utils;

import android.util.Log;

import androidx.annotation.NonNull;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameTweetsBank {

    public static final String TAG = "GameTweetsBank";

    // Amount of tweets to be randomly selected from tweets object
    private static final int TWEETS_TOTAL_PICK_MIN = 30;
    private static final int TWEETS_TOTAL_PICK_MAX = 50;
    private static final int TWEETS_FRIEND_PICK_MAX = 10;
    private static final int FRIENDS_PICK_MAX = 5;

    // Twitter client for fetching data from endpoints
    private final TwitterClient client = new TwitterClient();

    // Game Question History
    private final List<JSONObject> gameQuestionHistory;

    // Question Bank
    private final Stack<Tweet> gameQuestionBank;

    // Used Tweet Id Set
    private final Set<String> usedTweetSet;

    // Map of Friends to a list of Tweets
    private final static Map<String, List<Tweet>> friend_tweet_map = new HashMap<>();
    private final static Map<String, String> friend_id_name_map = new HashMap<>();
    private final static List<String> friend_ids = new ArrayList<>();

    public GameTweetsBank() {
        this.gameQuestionHistory = new ArrayList<>();
        this.gameQuestionBank = new Stack<>();
        this.usedTweetSet = new HashSet<>();
        try {
            fillQuestionBank();
        } catch (JSONException e) {
            Log.e(TAG, "Error handling JSON", e);
        }
    }

    private void getFriendsIdsHelper(JSONArray friendIdsArray) throws JSONException {
        // Add friends to hashmap
        for(int i = 0; i < friendIdsArray.length(); i++) {
            String friend_id = friendIdsArray.getString(i);
            friend_tweet_map.put(friend_id, new ArrayList<>());
            friend_ids.add(friend_id);
        }
    }

    /**
     * Returns a list of friend ids given a JSONArray of friend_ids returned by Twitter API
     * @return List of Twitter friend ids
     */
    private List<String> getFriendsIds() {
        if (!friend_ids.isEmpty()) return friend_ids;

        // Fetch from endpoint (if friend List !isEmpty, return stored list)
        client.fetchFriendIds(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Friend Fetch Failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i(TAG, "SUCCESS!");
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);

                        JSONArray friendIdsArray = json.getJSONArray("ids");
                        getFriendsIdsHelper(friendIdsArray);
                    } catch (JSONException ignored) {
                    }
                }
            }
        });

        // Return list
        return friend_ids;
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
                this.gameQuestionBank.push(tweet);
                i++;
            }
            if (this.gameQuestionBank.size() == TWEETS_TOTAL_PICK_MAX) return;
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
            String[] randomlyChosenFriends = getRandomFriends(!friend_ids.isEmpty() ? friend_ids : getFriendsIds());

            // Loop through each friend id from randomly generated array
            for (String friendId : randomlyChosenFriends) {
                // Stores culled Tweets for a single friend
                if (friend_tweet_map.get(friendId) == null) {
                    client.fetchUserTimeline(friendId, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "Failed timeline fetch", e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            } else {
                                Log.i(TAG, "SUCCESS!");
                                try {
                                    String responseData = response.body().string();
                                    JSONArray tweetObjects = new JSONArray(responseData);
                                    List<Tweet> userTimeline = new ArrayList<>();
                                    if (tweetObjects.length() > 0) {
                                        for (int i = 0; i < tweetObjects.length(); i++) {
                                            userTimeline.add(Tweet.fromJson(tweetObjects.getJSONObject(i)));
                                        }

                                        friend_tweet_map.put(friendId, userTimeline);
                                        fillQuestionBankHelper(userTimeline);
                                    }
                                } catch (JSONException ignored) {
                                }
                            }
                        }
                    });
                } else {
                    fillQuestionBankHelper(friend_tweet_map.get(friendId));
                }
            }
        } while(this.gameQuestionBank.size() < TWEETS_TOTAL_PICK_MIN);
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
            if (this.gameQuestionBank.empty()) fillQuestionBank();
        } catch (JSONException ignored) {
        }

        Tweet tweet;
        tweet = this.gameQuestionBank.pop();
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
        final List<String> userOptionIds = new ArrayList<>(3);

        Random random = new Random();
        String randId;

        // add correct answer to cache for future use
        if (friend_id_name_map.get(tweet.getUser().getId()) != null)
            friend_id_name_map.put(tweet.getUser().getId(), tweet.getUser().getName());

        // Add 3 random incorrect answers
        for (int i = 0; i < 3; i++) {
            // select a random friend id not yet chosen and not the actual answer
            do {
                randId = friend_ids.get(random.nextInt(friend_ids.size()));
            } while (randId.equals(tweet.getUser().getId()) && userOptionIds.contains(randId));

            if (friend_id_name_map.get(randId) != null) {
                userOptions.add(friend_id_name_map.get(randId));
                userOptionIds.add(randId);
                continue;
            }

            TwitterClient.getUser(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Callback failed", e);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Log.i(TAG, "SUCCESS!");
                        try {
                            String responseData = response.body().string();
                            JSONObject userObject = new JSONObject(responseData);
                            TweetUser friend = TweetUser.fromJson(userObject);
                            userOptions.add(friend.getName());
                            userOptionIds.add(friend.getId());

                            // Cache name for future use
                            friend_id_name_map.put(friend.getId(), friend.getName());
                        } catch (JSONException ignored) {
                        }
                    }
                }
            }, randId);
        }

        return new Pair<>(tweet, userOptions.toArray(new String[]));
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
}
