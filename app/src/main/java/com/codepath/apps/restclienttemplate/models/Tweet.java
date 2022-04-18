package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.utils.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    String body = "";
    String createdAt;
    String id;
    String relativeTimestamp;
    String timestamp;
    // Leaving commented until we require images to be loaded
//    String nativeImageUrl;
//    HashMap<String, int[]> nativeImagePair = new HashMap<>();
    TweetUser user;
    int retweetCount;
    int favoriteCount;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject tweetJSONObject) throws JSONException {
        Tweet tweet = new Tweet();

        // Code for getting images from a tweet, leaving commented until needed
//        try {
            JSONObject nativeImageDimens;
            int[] dimens;
//            JSONObject mediaObject = getTwimage(jsonObject.getJSONObject("entities").getJSONArray("media"));
//            if(mediaObject != null) {
//                tweet.nativeImageUrl = mediaObject.getString("media_url_https");
//                nativeImageDimens = mediaObject.getJSONObject("sizes").getJSONObject("medium");
//                dimens = new int[]{nativeImageDimens.getInt("w"), nativeImageDimens.getInt("h")};
//                tweet.nativeImagePair.put(tweet.nativeImageUrl, dimens);
//            }
//        } catch (JSONException ignored) {
//        }
        tweet.id = tweetJSONObject.getString("id_str");
        tweet.body += tweetJSONObject.getString("full_text");
        tweet.createdAt = tweetJSONObject.getString("created_at");
        tweet.user = TweetUser.fromJson(tweetJSONObject.getJSONObject("user"));
        tweet.timestamp = TimeFormatter.getTimeStamp(tweet.createdAt);
        tweet.relativeTimestamp = TimeFormatter.getTimeDifference(tweet.createdAt);
        tweet.retweetCount = tweetJSONObject.getInt("retweet_count");
        tweet.favoriteCount = tweetJSONObject.getInt("favorite_count");

        return tweet;
    }

//    /*
//        Gets the first image associated with a tweet (Leaving commented until needed)
//     */
//    private static JSONObject getTwimage(JSONArray media) throws JSONException {
//        for(int i = 0; i < media.length(); i++) {
//            JSONObject mediaObject = media.getJSONObject(i);
//            if(mediaObject.getString("type").equals("photo")) {
//                return mediaObject;
//            }
//        }
//        return null;
//    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;

    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getRelativeTimestamp() {
        return relativeTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TweetUser getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }
}

