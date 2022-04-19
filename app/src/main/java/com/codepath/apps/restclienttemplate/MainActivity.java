package com.codepath.apps.restclienttemplate;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapters.MainActivityViewPagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityMainBinding;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityNavigator;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainActivityNavigator {

    ActivityMainBinding binding;
    MainActivityViewPagerAdapter viewPagerAdapter;
    ViewPager2.OnPageChangeCallback pageChangeCallback;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        viewPagerAdapter = new MainActivityViewPagerAdapter(this, this);
        binding.vp2Main.setAdapter(viewPagerAdapter);

        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bnvMain.getMenu().getItem(position).setChecked(true);
            }
        };
        binding.vp2Main.registerOnPageChangeCallback(pageChangeCallback);

        binding.bnvMain.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                boolean shouldSmoothScroll = true;
                while (fragmentManager.getBackStackEntryCount() != 0) {
                    fragmentManager.popBackStackImmediate();
                    shouldSmoothScroll = false;
                }
                refreshBackButtonVisibility();
                fragmentManager.popBackStack();
                if (id == R.id.itemPlay) {
                    binding.vp2Main.setCurrentItem(0, shouldSmoothScroll);
                }
                else if (id == R.id.itemLeaderboard) {
                    binding.vp2Main.setCurrentItem(1, shouldSmoothScroll);
                }
                else if (id == R.id.itemProfile) {
                    binding.vp2Main.setCurrentItem(2, shouldSmoothScroll);
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pageChangeCallback != null) {
            binding.vp2Main.unregisterOnPageChangeCallback(pageChangeCallback);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshBackButtonVisibility() {
        Objects.requireNonNull(
                getSupportActionBar()
        ).setDisplayHomeAsUpEnabled(fragmentManager.getBackStackEntryCount() != 0);
    }

    @Override
    public void requestOverlay(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fcvMain.getId(), fragment)
                .addToBackStack(String.valueOf(binding.fcvMain.getId()))
                .commit();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        refreshBackButtonVisibility();
    }
}