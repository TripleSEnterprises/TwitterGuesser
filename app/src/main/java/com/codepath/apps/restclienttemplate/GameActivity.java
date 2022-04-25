package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;

import com.codepath.apps.restclienttemplate.databinding.ActivityGameBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.GameTweetsBank;
import com.google.android.material.button.MaterialButton;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    public static final String TAG = "GameActivity";

    // Current Question Being Answered
    final GameTweetsBank gameTweetsBank = new GameTweetsBank();
    Pair<Tweet, String[]> question = gameTweetsBank.getQuestion();
    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_game);
        Tweet tweet = question.first;

        // Setting tweet user variable populates all of the tweet elements in layout
        binding.setTweetUser(tweet.getUser());

        // Setting tweet variable populates image, screen name and username in layout
        binding.setTweet(tweet);

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateScore(0);

        setupOptions();

    }

    // Updates Score string on top of screen
    private void updateScore(double score){
        binding.tvGameScore.setText(this.getString(
                R.string.game_score,
                score
        ));
    }

    class onOptionClickListener implements View.OnClickListener {
        private boolean isCorrect(String name) {
            return question.first.getUser().getName().equals(name);
        }

        @Override
        public void onClick(View view) {
            final String btnLabel = ((TextView) view).getText().toString();
            if(isCorrect(btnLabel)) {

            } else {

            }
        }
    }

    // TODO: set up the questions
    private void setupOptions(){
        String correctOption = question.first.getUser().getScreenName();
        String[] incorrectOptions = question.second;
        MaterialButton[] optButtons = {binding.btnFirst, binding.btnSecond, binding.btnThird, binding.btnFourth};
        String[] options = new String[incorrectOptions.length + 1];

        // Fill available options
        options[0] = correctOption;
        System.arraycopy(incorrectOptions, 0, options, 1, incorrectOptions.length);

        // Randomly assign buttons
        Random random = new Random();
        String randOpt;
        int randIdx;
        for(int i = 0; i < options.length; i++) {
            // Select random option
            if (i == options.length - 1) {
                int j = 0;
                while (options[j++].isEmpty()) ;
                optButtons[i].setText(options[j - 1]);
                break;
            }
            do {
                randIdx = random.nextInt(options.length);
                randOpt = options[randIdx];
            } while (randOpt.isEmpty());
            options[randIdx] = "";
            optButtons[i].setText(randOpt);
            optButtons[i].setOnClickListener(new onOptionClickListener());
        }
        // TODO: set onClicks for correct answer(Update Score,change to green,move to next round)

        // TODO: set onClick for incorrect answer(Update Score, change button colors,move to end Game)

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

        // TODO: Un-redact losing tweet

        // TODO: Set up game log recyclerview

    }

    // Sets up next round layout
    private void nextRound() {
        // Add next button
        binding.btnNext.setVisibility(View.VISIBLE);
        // TODO: Load new Question and option once the button is clicked and remove from layout
    }

    private void nextButtonOnClickCallback() {
        // Next button logic after reveal
    }
}