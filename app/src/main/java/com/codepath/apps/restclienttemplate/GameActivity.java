package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.databinding.ActivityGameBinding;


public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    public static final String TAG = "GameActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_game);

        // Setting tweet user variable populates all of the tweet elements in layout
        binding.setTweetUser(null);

        // Setting tweet variable populates image, screen name and username in layout
        binding.setTweet(null);

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateScore(15);

        setupOptions();



    }

    // Updates Score string on top of screen
    private void updateScore(double score){
        binding.tvGameScore.setText(this.getString(
                R.string.game_score,
                score
        ));
    }

    // TODO: set up the questions
    private void setupOptions(){
        binding.btnFirst.setText("One");
        binding.btnSecond.setText("Two");
        binding.btnThird.setText("Three");
        binding.btnFourth.setText("Four");
        // TODO: set onClicks for correct answer(Update Score,change to green,move to next round)

        // TODO: set onClick for incorrect answer(Update Score, change button colors,move to end Game)

    }

    // Sets up end game layout
    private void endGameScreen(){
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
    private void nextRound(){
        // Add next button
        binding.btnNext.setVisibility(View.VISIBLE);
        // TODO: Load new Question and option once the button is clicked and remove from layout

    }



}