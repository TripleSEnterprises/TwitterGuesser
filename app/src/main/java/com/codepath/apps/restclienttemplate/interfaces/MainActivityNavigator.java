package com.codepath.apps.restclienttemplate.interfaces;

import androidx.fragment.app.Fragment;

public interface MainActivityNavigator {
    void requestOverlay(Fragment fragment);
    void viewPagerNavigate(int fragmentIndex);
}
