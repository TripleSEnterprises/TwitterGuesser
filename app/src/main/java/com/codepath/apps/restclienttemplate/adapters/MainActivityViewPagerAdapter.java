package com.codepath.apps.restclienttemplate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.apps.restclienttemplate.LeaderboardFragment;
import com.codepath.apps.restclienttemplate.ProfileFragment;
import com.codepath.apps.restclienttemplate.StartGameFragment;
import com.parse.ParseUser;

import java.security.InvalidParameterException;

public class MainActivityViewPagerAdapter extends FragmentStateAdapter {

    private static final int SCREEN_COUNT = 3;

    private static final int START_GAME_FRAGMENT_INDEX = 0;
    private static final int LEADERBOARD_FRAGMENT_INDEX = 1;
    private static final int PROFILE_FRAGMENT_INDEX = 2;

    public MainActivityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case START_GAME_FRAGMENT_INDEX:
                return StartGameFragment.newInstance(ParseUser.getCurrentUser());
            case LEADERBOARD_FRAGMENT_INDEX:
                return LeaderboardFragment.newInstance();
            case PROFILE_FRAGMENT_INDEX:
                return ProfileFragment.newInstance(ParseUser.getCurrentUser());
            default:
                throw new InvalidParameterException();
        }
    }

    @Override
    public int getItemCount() {
        return SCREEN_COUNT;
    }
}
