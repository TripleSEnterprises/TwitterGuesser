package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import javax.annotation.Nullable;

public class GameDeserialized {
    public static String[] getTweetIdsFromSerialized(JSONObject game) throws JSONException{
        JSONArray jsonArray = game.getJSONArray("question");
        int questionCount = jsonArray.length();
        String[] twitterIds = new String[questionCount];

        for (int i = 0; i < questionCount; i++) {
            JSONObject questionObject = jsonArray.getJSONObject(i);
            twitterIds[i] = questionObject.getString("tweet_id");
        }
        return twitterIds;
    }

    private final Question[] questions;

    public static GameDeserialized fromJSON(JSONObject game, Hashtable<String, Tweet> tweets) throws JSONException {
        JSONArray jsonArray = game.getJSONArray("question");
        int questionCount = jsonArray.length();
        Question[] questionArray = new Question[questionCount];

        for (int i = 0; i < questionCount; i++) {
            JSONObject questionObject = jsonArray.getJSONObject(i);
            String tweetId = questionObject.getString("tweet_id");
            questionArray[i] = new Question(
                    tweetId,
                    questionObject.getDouble("score"),
                    tweets.containsKey(tweetId) ? tweets.get(tweetId) : null
            );
        }

        return new GameDeserialized(questionArray);
    }

    private GameDeserialized(Question[] questions) {
        this.questions = questions;
    }

    public Question getQuestion(int index) {
        if (index >= 0 && index < this.questionCount()) return questions[index];
        throw new IndexOutOfBoundsException("Invalid Question Index");
    }

    public int questionCount() {
        return questions.length;
    }


    public static class Question {
        private final String tweetId;
        private final double score;
        @Nullable private final Tweet tweet;

        private Question(String tweetId, double score, @Nullable Tweet tweet) {
            this.tweetId = tweetId;
            this.score = score;
            this.tweet = tweet;
        }

        public String getTweetId() {
            return tweetId;
        }

        @Nullable
        public Tweet getTweet() { return tweet; }

        public Double getScore() {
            return score;
        }

        public boolean isLoss() {
            return score == 0.0;
        }
    }
}
