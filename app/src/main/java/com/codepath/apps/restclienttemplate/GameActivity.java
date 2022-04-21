package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.databinding.ActivityGameBinding;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    public static final String TAG = "GameActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_game);
        Timer timer = new Timer();
        updateScore(15);
        startGameTimer(timer);


        binding.setTweetUser(null);
        binding.setTweet(null);

    }

    // Updates Score string on top of screen
    private void updateScore(double score){
        binding.tvGameScore.setText(this.getString(
                R.string.game_score,
                score
        ));
    }

    // Starts a timer that lasts 15 seconds. Score is updated at the end
    private void startGameTimer(Timer timer){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateScore(0);
                Log.i(TAG,"Timer ended");
            }
        },15000);
    }
}