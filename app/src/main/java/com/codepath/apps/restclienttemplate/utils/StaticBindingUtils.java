package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Game;
import com.codepath.apps.restclienttemplate.models.GameDeserialized;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class StaticBindingUtils {
    private static final String TAG = "StaticBindingUtils";

    private static final String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

//    private static final String[] likes_suffixes = new String[] { "K", "M", "B"};

    @BindingAdapter("profilePictureSetter")
    public static void profilePictureSetter(ImageView imageView, ParseUser user) {
        UserDataConflictResolver.profileImageResolver(imageView, user);
    }

    @BindingAdapter("profileGameHistoryHighScore")
    public static void profileGameHistoryHighScore(TextView textView, Game game) {
        textView.setText(String.format(Locale.US, "%.3f", game.getFinalScore().doubleValue()));
    }

    @BindingAdapter("viewClipToBackground")
    public static void viewClipToBackground(ImageView imageView, boolean bool) {
        imageView.setClipToOutline(bool);
    }

    @BindingAdapter("profileMatchHistoryDateFormatter")
    public static void profileMatchHistoryDateFormatter(TextView textView, Game game) {
        textView.setText(new SimpleDateFormat("M/dd/yyyy", Locale.US).format(game.getCreatedAt()));
    }
    @BindingAdapter("profilePositionFormatter")
    public static void profilePositionFormatter(TextView textView, ParseUser user) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class)
                .whereGreaterThan("highScore", user.getNumber("highScore"));
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                int position = count + 1;
                if (e == null) {
                    String suffix;
                    switch (position % 100) {
                        case 11:
                        case 12:
                        case 13:
                            suffix = "th";
                            break;
                        default:
                            suffix = suffixes[(int) (position % 10)];
                    }
                    textView.setText(textView.getContext().getString(
                            R.string.profile_leaderboard_position,
                            position,
                            suffix)
                    );
                }
                else {
                    Log.wtf(TAG, e);
                }
            }
        });
    }

    @BindingAdapter("profileHighScoreFormatter")
    public static void profileHighScoreFormatter(TextView textView, ParseUser user) {
        Number highScoreFromDB = user.getNumber("highScore");
        double highScore = highScoreFromDB != null? highScoreFromDB.doubleValue() : 0;
        textView.setText(textView.getContext().getString(
                R.string.profile_high_score,
                highScore
        ));
    }

    @BindingAdapter("detailScoreFormatter")
    public static void detailScoreFormatter(TextView textView, Game game) {
        textView.setText(textView.getContext().getString(
                R.string.detail_score,
                game.getFinalScore().doubleValue()
        ));
    }

    @BindingAdapter("leaderboardHighScoreFormatter")
    public static void leaderboardHighScoreFormatter(TextView textView, ParseUser user){
        Number highScoreFromDB = user.getNumber("highScore");
        double highScore = highScoreFromDB != null? highScoreFromDB.doubleValue() : 0;
        textView.setText(textView.getContext().getString(
                R.string.leaderboard_high_score,
                highScore
        ));
    }

    @BindingAdapter("leaderboardSelfPositionFormatter")
    public static void leaderboardSelfPositionFormatter(TextView textView, ParseUser user){
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class)
                .whereGreaterThan("highScore", user.getNumber("highScore"));
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                int position = count + 1;
                if (e == null) {
                    textView.setText(String.valueOf(position));
                }
                else {
                    Log.wtf(TAG, e);
                }
            }
        });
    }

    @BindingAdapter("gamePictureSetter")
    public static void gamePictureSetter(ImageView imageView, String url){
        UserDataConflictResolver.setImageViewWithURLOnMainThread(
                imageView,
                url == null? null : url.replace("normal", "bigger"));
    }

    @BindingAdapter("screenNameSetter")
    public static void screenNameSetter(TextView textView, String username){
        if( username == null){
            textView.setText(textView.getContext().getString(R.string.redacted));
        }
        else {
            textView.setText(textView.getContext().getString(
                    R.string.twitter_username,
                    username
            ));
        }
    }

    @BindingAdapter("userNameSetter")
    public static void userNameSetter(TextView textView, String username){
        if( username == null){
            textView.setText(textView.getContext().getString(R.string.redacted));
        }
        else {
            textView.setText(username);
        }
    }

    @BindingAdapter("tweetBodySetter")
    public static void tweetBodySetter(TextView textView, String body){
        if(body == null){
            textView.setText(textView.getContext().getString(R.string.tweet_body_default));
        }
        else{
            textView.setText(body);
        }
    }

    @BindingAdapter("retweetSetter")
    public static void retweetSetter(TextView textView, int retweetCount){
        textView.setText(String.valueOf(retweetCount));
    }

    @BindingAdapter("likesSetter")
    public static void likesSetter(TextView textView, int favoriteCount){
        String formattedString = String.valueOf(favoriteCount);
        if(favoriteCount >= 1000000000){
            formattedString = String.format(Locale.US,"%.1fB", favoriteCount/ 1000000000.0);
        }

        else if(favoriteCount >= 1000000){
            formattedString =  String.format(Locale.US,"%.1fM", favoriteCount/ 1000000.0);
        }
        else if(favoriteCount >=1000){
            formattedString =  String.format(Locale.US,"%.1fK", favoriteCount/ 1000.0);
        }
        textView.setText(formattedString);
    }

    @BindingAdapter("timestampSetter")
    public static void timestampSetter(TextView textView, String timestamp){
        if(timestamp == null){
            textView.setText("Loading timestamp");
            return;
        }
        textView.setText(timestamp);
    }

    @BindingAdapter("setQuestionScore")
    public static void setQuestionScore(TextView textView, GameDeserialized.Question question) {
        if (question.isLoss()) {
            textView.setText(R.string.loss_text);
        } else {
            textView.setText(String.format(Locale.US,"+%.3f", question.getScore()));
        }
    }

    @BindingAdapter("setQuestionScoreColor")
    public static void setQuestionScoreColor(TextView textView, GameDeserialized.Question question) {
        Resources resources = textView.getContext().getResources();
        textView.setTextColor(question.isLoss()?
                resources.getColor(R.color.wrong_answer) :
                resources.getColor(R.color.right_answer)
        );
    }

    @BindingAdapter("setTweetBodyWithFallback")
    public static void setTweetBodyWithFallback(TextView textView, Tweet tweet) {
        if (tweet != null) {
            textView.setText(tweet.getBody());
        } else {
            textView.setText(R.string.tweet_unavailable);
        }
    }

    @BindingAdapter("goToBrowserUrlOnClick")
    public static void goToBrowserUrlOnClick(View view, String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    @BindingAdapter("revealContentSensitivityBanner")
    public static void revealContentSensitivityBanner(View view, ParseUser user) {
        boolean contentSensitivityOn = user.getBoolean("profanityFilter");
        if(contentSensitivityOn) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
