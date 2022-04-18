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

import com.codepath.apps.restclienttemplate.adapters.LeaderboardPlayersAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentLeaderboardBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {
    private FragmentLeaderboardBinding binding;
    public static final String TAG = "LeaderboardFragment";
    public static final String ARG_USER = "user";

    private LeaderboardPlayersAdapter adapter;
    private ArrayList<ParseUser> users;
    private ParseUser user;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(ParseUser user) {
        LeaderboardFragment fragment = new LeaderboardFragment();
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
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        try {
            binding.setUser(user.fetch());
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>(100);
        binding.tvTopPlayersTitle.setText("Top 100 Players");

        adapter = new LeaderboardPlayersAdapter(users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        binding.rvTopPlayers.setLayoutManager(linearLayoutManager);
        binding.rvTopPlayers.setAdapter(adapter);

        ParseClient.getTopPlayers(0,new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> topplayers, ParseException e) {
                if(e!=null){
                    Log.e(TAG,"Couldn't get top users",e);
                    return;
                }
                users.addAll(topplayers);
                adapter.notifyItemRangeInserted(0,users.size());
                for (ParseUser user: topplayers) {
                    Log.i(TAG,user.getUsername()+" "+user.get("highScore"));
                }

            }
        });
    }
}