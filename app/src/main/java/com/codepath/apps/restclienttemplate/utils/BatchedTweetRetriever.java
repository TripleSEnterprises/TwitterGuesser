package com.codepath.apps.restclienttemplate.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BatchedTweetRetriever {
    private static final String TAG = "BatchedTweetRetriever";

    private static final int MAX_TWEETS_PER_BATCH = 100;

    private static final Object MUTEX_LOCK = new Object();

    public interface BatchTweetCallback {
        void allTweetsReady(Hashtable<String, Tweet> batchedTweets);
    }


    public static void getTweets(BatchTweetCallback callback, String ... id) {
        Hashtable<String, Tweet> batchedTweetObjects = new Hashtable<>();
        int batchCount = (id.length / MAX_TWEETS_PER_BATCH);

        for (int batchNumber = 0; batchNumber <= batchCount; batchNumber++) {
            int startIndex = batchNumber * MAX_TWEETS_PER_BATCH;
            int endIndex = Math.min((batchNumber + 1) * MAX_TWEETS_PER_BATCH - 1, id.length - 1);
            if (endIndex < startIndex) break;
            String[] batchIds = Arrays.copyOfRange(id, startIndex, endIndex + 1);

            TwitterClient.fetchTweets(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    synchronized (MUTEX_LOCK) {
                        try {
                            JSONObject tweetMap = new JSONObject(response.body().string());
                            tweetMap = tweetMap.getJSONObject("id");

                            Iterator<String> tweetMapIds = tweetMap.keys();
                            while(tweetMapIds.hasNext()) {
                                String tweetId = tweetMapIds.next();
                                try {
                                    JSONObject tweet = tweetMap.getJSONObject(tweetId);
                                    batchedTweetObjects.put(tweetId, Tweet.fromJson(tweet));
                                } catch (JSONException e) {
                                    Log.e(TAG, "onResponse: Couldn't load Tweet " + tweetId, e);
                                    batchedTweetObjects.put(tweetId, null);
                                }
                            }
                            if (batchedTweetObjects.size() == id.length) {
                                callback.allTweetsReady(batchedTweetObjects);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: Tweet Batch", e);
                        }
                    }
                }
            }, batchIds);
        }
    }

}
