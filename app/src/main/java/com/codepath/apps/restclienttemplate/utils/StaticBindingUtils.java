package com.codepath.apps.restclienttemplate.utils;

import android.util.Log;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.codepath.apps.restclienttemplate.R;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class StaticBindingUtils {
    private static final String TAG = "StaticBindingUtils";

    private static final String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

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
        int highScore = highScoreFromDB != null? highScoreFromDB.intValue() : 0;
        textView.setText(textView.getContext().getString(
                R.string.profile_high_score,
                highScore
        ));
    }
}
