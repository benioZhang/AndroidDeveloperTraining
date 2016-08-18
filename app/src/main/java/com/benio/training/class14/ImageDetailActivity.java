package com.benio.training.class14;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.benio.training.R;

public class ImageDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_IMAGE = "extra_image";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        PagerAdapter pagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), 100);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(pagerAdapter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Utils.hasHoneycomb()) {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                // Hide and show the ActionBar as the visibility changes
                mViewPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                            actionBar.hide();
                        } else {
                            actionBar.show();
                        }
                    }
                });
                // Start low profile mode and hide ActionBar
                mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                actionBar.hide();
            }
        }

        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mViewPager.setCurrentItem(extraCurrentItem, false);
        }
    }

    @Override
    public void onClick(View v) {
        int visibility = mViewPager.getSystemUiVisibility();
        if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    static class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            this.mSize = size;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(Images.IMAGES[position % Images.IMAGES.length]);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }
}
