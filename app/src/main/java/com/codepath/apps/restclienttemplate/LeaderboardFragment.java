package com.codepath.apps.restclienttemplate;

import static com.codepath.apps.restclienttemplate.adapters.MainActivityViewPagerAdapter.PROFILE_FRAGMENT_INDEX;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.adapters.LeaderboardPlayersAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentLeaderboardBinding;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityNavigator;
import com.parse.FindCallback;
import com.parse.GetCallback;
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

    public MainActivityNavigator mainActivityNavigator;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(ParseUser user, MainActivityNavigator mainActivityNavigator) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        fragment.mainActivityNavigator= mainActivityNavigator;
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
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>(100);
        binding.tvTopPlayersTitle.setText("Top 100 Players");

        adapter = new LeaderboardPlayersAdapter(users,mainActivityNavigator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        binding.rvTopPlayers.setLayoutManager(linearLayoutManager);
        binding.rvTopPlayers.setAdapter(adapter);

        getPlayers();

        binding.rlSelfProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityNavigator.viewPagerNavigate(PROFILE_FRAGMENT_INDEX);
            }
        });

        binding.srlLeaderboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                user.fetchInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        binding.setUser(object);
                    }
                });
                getPlayers();
            }
        });
    }

    private void getPlayers(){
        ParseClient.getTopPlayers(0,new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> topplayers, ParseException e) {
                if(e!=null){
                    Log.e(TAG,"Couldn't get top users",e);
                    return;
                }
                binding.srlLeaderboard.setRefreshing(false);
                adapter.clear();
                adapter.addAll(topplayers);
            }
        });
    }
}