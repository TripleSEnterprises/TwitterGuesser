package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.databinding.FragmentStartGameBinding;
import com.parse.ParseException;
import com.parse.ParseUser;

public class StartGameFragment extends Fragment {
    private FragmentStartGameBinding binding;
    public static final String TAG = "StartGameFragment";
    public static final String ARG_USER = "user";

    private ParseUser user;

    public StartGameFragment() {
        // Required empty public constructor
    }

    public static StartGameFragment newInstance(ParseUser user) {
        StartGameFragment fragment = new StartGameFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartGameBinding.inflate(inflater, container, false);
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnStartGame.setOnClickListener(v->{
            //Go to game
        });
    }
}