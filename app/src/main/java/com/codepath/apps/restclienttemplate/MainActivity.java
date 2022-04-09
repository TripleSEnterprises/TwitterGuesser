package com.codepath.apps.restclienttemplate;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapters.MainActivityViewPagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainActivityViewPagerAdapter viewPagerAdapter;
    ViewPager2.OnPageChangeCallback pageChangeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewPagerAdapter = new MainActivityViewPagerAdapter(this);
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
                if (id == R.id.itemPlay) {
                    binding.vp2Main.setCurrentItem(0, true);
                }
                else if (id == R.id.itemLeaderboard) {
                    binding.vp2Main.setCurrentItem(1, true);
                }
                else if (id == R.id.itemProfile) {
                    binding.vp2Main.setCurrentItem(2, true);
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
}