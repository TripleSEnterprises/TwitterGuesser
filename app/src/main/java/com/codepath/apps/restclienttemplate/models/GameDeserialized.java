package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameDeserialized {
    private final Question[] questions;

    public static GameDeserialized fromJSON(JSONObject Game) throws JSONException {
        JSONArray jsonArray = Game.getJSONArray("question");
        int questionCount = jsonArray.length();
        Question[] questionArray = new Question[questionCount];

        for (int i = 0; i < questionCount; i++) {
            JSONObject questionObject = jsonArray.getJSONObject(i);
            questionArray[i] = new Question(
                    questionObject.getString("tweet_id"),
                    questionObject.getDouble("score")
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

        private Question(String tweetId, double score) {
            this.tweetId = tweetId;
            this.score = score;
        }

        public String getTweetId() {
            return tweetId;
        }

        public Double getScore() {
            return score;
        }

        public boolean isLoss() {
            return score == 0.0;
        }
    }
}
