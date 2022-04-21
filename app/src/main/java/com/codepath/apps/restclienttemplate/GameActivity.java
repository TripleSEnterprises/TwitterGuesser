package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.databinding.ActivityGameBinding;

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

        updateScore(0);

    }

    // Updates Score string on top of screen
    private void updateScore(double score){
        binding.tvGameScore.setText(this.getString(
                R.string.game_score,
                score
        ));
    }
}