package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.adapters.GameDetailGameHistoryAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentGameDetailBinding;
import com.codepath.apps.restclienttemplate.models.Game;
import com.codepath.apps.restclienttemplate.models.GameDeserialized;

import org.json.JSONException;
import org.json.JSONObject;

public class GameDetailFragment extends Fragment {
    private static final String TAG = "GameDetailFragment";

    private FragmentGameDetailBinding binding;

    private static final String ARG_GAME = "game";

    private Game game;
    private GameDeserialized gameDeserialized;

    public GameDetailFragment() {
        // Required empty public constructor
    }

    public static GameDetailFragment newInstance(Game game) {
        GameDetailFragment fragment = new GameDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = getArguments().getParcelable(ARG_GAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setGame(game);
        try {
            gameDeserialized = GameDeserialized.fromJSON(game.getQuestions());
        } catch (JSONException e) {
            Log.e(TAG, "onViewCreated: ", e);
            return;
        }
        GameDetailGameHistoryAdapter gameHistoryAdapter = new GameDetailGameHistoryAdapter(gameDeserialized);
        binding.rvGameHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvGameHistory.setAdapter(gameHistoryAdapter);
        
    }
}