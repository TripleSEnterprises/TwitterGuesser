package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.adapters.ProfileGameHistoryAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentProfileBinding;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityNavigator;
import com.codepath.apps.restclienttemplate.models.Game;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;

    private static final String ARG_USER = "user";

    private ParseUser user;
    private ProfileGameHistoryAdapter adapter;
    private ArrayList<Game> games;
    private EndlessRecyclerViewScrollListener scrollListener;

    public MainActivityNavigator mainActivityNavigator;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ParseUser user, MainActivityNavigator mainActivityNavigator) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        fragment.mainActivityNavigator = mainActivityNavigator;
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        games = new ArrayList<>(10);

        adapter = new ProfileGameHistoryAdapter(user, games, mainActivityNavigator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        binding.rvGameHistory.setLayoutManager(linearLayoutManager);
        binding.rvGameHistory.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view, RequestCompleteCallback callback) {
                Date lastGameDate = games.get(games.size() - 1).getCreatedAt();
                ParseClient.getMatchHistory(user, lastGameDate, new FindCallback<Game>() {
                    @Override
                    public void done(List<Game> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Failed To Get More Match History: ", e);
                            callback.requestComplete(false);
                        }
                        // Stop Getting More. resetState() would need to be called.
                        if (objects.size() == 0) {
                            callback.requestComplete(false);
                            return;
                        }

                        int lastIndex = games.size();
                        games.addAll(objects);
                        adapter.notifyItemRangeInserted(lastIndex, objects.size());

                        callback.requestComplete(true);
                    }
                });
            }
        };
        binding.rvGameHistory.addOnScrollListener(scrollListener);

        getMatches();

        binding.srlProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.srlProfile.setRefreshing(true);
                user.fetchInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        binding.srlProfile.setRefreshing(false);
                        binding.setUser(object);
                    }
                });

                games.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetState();
                getMatches();

            }
        });
    }

    private void getMatches(){
        ParseClient.getMatchHistory(user, new FindCallback<Game>() {
            @Override
            public void done(List<Game> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed To Get Match History: ", e);
                    return;
                }
                games.addAll(objects);
                adapter.notifyItemRangeInserted(0, objects.size());
            }
        });
    }
}