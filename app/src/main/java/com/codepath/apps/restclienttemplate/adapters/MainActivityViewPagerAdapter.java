package com.codepath.apps.restclienttemplate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.apps.restclienttemplate.LeaderboardFragment;
import com.codepath.apps.restclienttemplate.ProfileFragment;
import com.codepath.apps.restclienttemplate.StartGameFragment;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityNavigator;
import com.parse.ParseUser;

import java.security.InvalidParameterException;

public class MainActivityViewPagerAdapter extends FragmentStateAdapter {

    private static final int SCREEN_COUNT = 3;

    public static final int START_GAME_FRAGMENT_INDEX = 0;
    public static final int LEADERBOARD_FRAGMENT_INDEX = 1;
    public static final int PROFILE_FRAGMENT_INDEX = 2;

    MainActivityNavigator mainActivityNavigator;

    public MainActivityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, MainActivityNavigator mainActivityNavigator) {
        super(fragmentActivity);
        this.mainActivityNavigator = mainActivityNavigator;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ParseUser user = ParseUser.getCurrentUser();
        switch (position) {
            case START_GAME_FRAGMENT_INDEX:
                return StartGameFragment.newInstance(user);
            case LEADERBOARD_FRAGMENT_INDEX:
                return LeaderboardFragment.newInstance(user, mainActivityNavigator);
            case PROFILE_FRAGMENT_INDEX:
                return ProfileFragment.newInstance(user, mainActivityNavigator);
            default:
                throw new InvalidParameterException();
        }
    }

    @Override
    public int getItemCount() {
        return SCREEN_COUNT;
    }
}
