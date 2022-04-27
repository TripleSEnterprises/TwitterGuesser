package com.codepath.apps.restclienttemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.apps.restclienttemplate.adapters.GameDetailGameHistoryAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityGameBinding;
import com.codepath.apps.restclienttemplate.models.GameDeserialized;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetUser;
import com.codepath.apps.restclienttemplate.utils.GameTweetsBank;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    public static final String TAG = "GameActivity";
    Long time_start, time_end;

    // Current Question Being Answered
    GameTweetsBank gameTweetsBank;
    Pair<Tweet, String[]> question;
    Tweet tweet;
    MaterialButton[] optButtons;
    Number finalScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        new QuestionBankTask().execute();
        finalScore = 0;
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_game);

        optButtons = new MaterialButton[]{binding.btnFirst, binding.btnSecond, binding.btnThird, binding.btnFourth};

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update Highscore
                ParseUser user = ParseUser.getCurrentUser();
                Number currentHighScore = user.getNumber("highScore");
                if(currentHighScore.doubleValue() < finalScore.doubleValue()) {
                    ParseClient.updateUserHighScore(ParseUser.getCurrentUser(), finalScore);
                }

                // Insert Result
                ParseClient.insertGameResult(gameTweetsBank.getUsedTweets().first, finalScore, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e(TAG,"Couldn't save game result",e);
                            return;
                        }
                        Toast.makeText(GameActivity.this, "Game Saved!", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextRound();
            }
        });

        binding.btnFirst.setOnClickListener(new onOptionClickListener());
        binding.btnSecond.setOnClickListener(new onOptionClickListener());
        binding.btnThird.setOnClickListener(new onOptionClickListener());
        binding.btnFourth.setOnClickListener(new onOptionClickListener());

        updateScore(0);

        time_start = System.nanoTime();

    }

    private class QuestionBankTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //do your request in here so that you don't interrupt the UI thread
            gameTweetsBank = new GameTweetsBank();
            try {
                gameTweetsBank.fillFriendsIds();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            //Here you are done with the task
            question = gameTweetsBank.getQuestion();
            tweet = question.first;
            // Setting tweet user variable populates all of the tweet elements in layout
            binding.setTweetUser(null);

            // Setting tweet variable populates image, screen name and username in layout
            binding.setTweet(tweet);
            setupOptions();
        }
    }
    // Updates Score string on top of screen
    private void updateScore(double score){
        finalScore = finalScore.doubleValue() + score;
        binding.tvGameScore.setText(this.getString(
                R.string.game_score,
                finalScore.doubleValue()
        ));
    }

    private void setEnabledOfAllButtons(boolean enabled) {
        binding.btnFirst.setEnabled(enabled);
        binding.btnSecond.setEnabled(enabled);
        binding.btnThird.setEnabled(enabled);
        binding.btnFourth.setEnabled(enabled);
    }

    private void setDefaultColorOfAllButtons() {
        binding.btnFirst.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
        binding.btnSecond.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
        binding.btnThird.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
        binding.btnFourth.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
    }

    class onOptionClickListener implements View.OnClickListener {
        private boolean isCorrect(String name) {
            return question.first.getUser().getScreenName().equals(name);
        }

        /**
         * Calculates score for a single round of the game
         * @param time_start
         * @param time_end
         * @return max(min(15, 10/x), 1)
         */
        private Number calculateScore(Long time_start, Long time_end) {
            Long time_delta_nano = time_end - time_start;
            return Math.max(Math.min(15, 10 / (time_delta_nano / Math.pow(10, 9))), 1);

        }

        @Override
        public void onClick(View view) {
            time_end = System.nanoTime();
            final String btnLabel = ((TextView) view).getText().toString();
            setEnabledOfAllButtons(false);

            if (isCorrect(btnLabel)) {
                binding.setTweetUser(tweet.getUser());
                Number roundScore = calculateScore(time_start, time_end);
                gameTweetsBank.addScore(question.first.getId(), roundScore);
                updateScore(roundScore.doubleValue());
                view.setBackgroundColor(getResources().getColor(R.color.right_answer));
                binding.btnNext.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.wrong_answer));
                endGameScreen();
            }
        }
    }

    private void setupOptions() {
        String correctOption = question.first.getUser().getId();
        String[] incorrectOptions = question.second;
        String[] options = new String[incorrectOptions.length + 1];
        Map<String, String> friend_name_map = gameTweetsBank.getFriendIdNameMap();

        // Fill available options
        options[0] = correctOption;
        System.arraycopy(incorrectOptions, 0, options, 1, incorrectOptions.length);

        // Randomly assign buttons
        Random random = new Random();
        int randIdx;
        String randOpt = "";
        List<String> randOptions = new ArrayList<>();
        List<String> optionScreenNames = new ArrayList<>();
        for(int i = 0; i < options.length; i++) {
            // Select random option
            if (i == options.length - 1) {
                int j = 0;
                while (options[j++].isEmpty()) ;
                randOpt = options[j - 1];
            } else {
                do {
                    randIdx = random.nextInt(options.length);
                    randOpt = options[randIdx];
                } while (randOpt.isEmpty());
                options[randIdx] = "";
                randOptions.add(randOpt);
            }

            String cachedScreenName = friend_name_map.get(randOpt);
            if (cachedScreenName != null) {
                randOptions.remove(randOpt);
                optionScreenNames.add(cachedScreenName);
            }
        }

        TwitterClient.getUsers(new Callback() {
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
                    GameActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String responseData = response.body().string();
                                JSONArray userObjects = new JSONArray(responseData);
                                JSONObject userObject;
                                for(int i = 0; i < userObjects.length(); i++) {
                                    userObject = userObjects.getJSONObject(i);
                                    TweetUser friend = TweetUser.fromJson(userObject);
                                    // Cache name for future use
                                    optionScreenNames.add(friend.getScreenName());
                                    gameTweetsBank.getFriendIdNameMap().put(friend.getId(), friend.getScreenName());
                                }
                            } catch (JSONException | IOException ignored) {
                            }
                        }
                    });
                }
            }
        }, randOptions.toArray(new String[randOptions.size()]));

        // Set onClicks
        MaterialButton button;
        String optionScreenName;
        for (int i = 0; i < optionScreenNames.size(); i++) {
            optionScreenName = optionScreenNames.get(i);
            button = optButtons[i];
            button.setText(optionScreenName);
        }
        setEnabledOfAllButtons(true);
    }


    // Sets up next round layout
    private void nextRound() {
        // Add next button
        binding.btnNext.setVisibility(View.INVISIBLE);
        setDefaultColorOfAllButtons();
        question = gameTweetsBank.getQuestion();
        tweet = question.first;
        // Setting tweet user variable populates all of the tweet elements in layout
        binding.setTweetUser(null);

        // Setting tweet variable populates image, screen name and username in layout
        binding.setTweet(tweet);
        setupOptions();
        time_start = System.nanoTime();
    }

    // Sets up end game layout
    private void endGameScreen() {
        // Remove buttons from layout
        binding.btnFirst.setVisibility(View.GONE);
        binding.btnSecond.setVisibility(View.GONE);
        binding.btnThird.setVisibility(View.GONE);
        binding.btnFourth.setVisibility(View.GONE);

        // Add title to layout
        binding.tvLoseTitle.setVisibility(View.VISIBLE);

        // Add Game log title and Recyclerview
        binding.tvGamelog.setVisibility(View.VISIBLE);
        binding.rvEndGameLog.setVisibility(View.VISIBLE);

        // Add Home Button to layout
        binding.btnHome.setVisibility(View.VISIBLE);

        binding.setTweetUser(question.first.getUser());

        Pair<JSONArray, Tweet[]> gameHistory = gameTweetsBank.getUsedTweets();

        try {
            GameDeserialized gameDeserialized = GameDeserialized.fromJSON(gameHistory.first, gameHistory.second);
            GameDetailGameHistoryAdapter gameHistoryAdapter = new GameDetailGameHistoryAdapter(gameDeserialized);
            binding.rvEndGameLog.setLayoutManager(new LinearLayoutManager(GameActivity.this));
            binding.rvEndGameLog.setAdapter(gameHistoryAdapter);
            binding.rvEndGameLog.invalidate();
        } catch (JSONException e) {
            Log.e(TAG, "endGameScreen: ", e);
        }
    }
}