package com.benio.training.class17;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.benio.training.R;

public class Class17Activity extends AppCompatActivity implements View.OnClickListener {
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class17);

        findViewById(R.id.btn_hideLoading).setOnClickListener(this);
        findViewById(R.id.btn_showLoading).setOnClickListener(this);
        findViewById(R.id.btn_pager).setOnClickListener(this);
        findViewById(R.id.btn_flip).setOnClickListener(this);
        findViewById(R.id.btn_zoom).setOnClickListener(this);
        findViewById(R.id.btn_layout).setOnClickListener(this);

        mContentView = findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading_spinner);

        // Initially hide the content view.
        mContentView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void hideLoading() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    private void showLoading() {
        mLoadingView.setAlpha(0f);
        mLoadingView.setVisibility(View.VISIBLE);

        mLoadingView.animate()
                .alpha(1)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mContentView.animate()
                .alpha(0)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mContentView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hideLoading:
                hideLoading();
                break;
            case R.id.btn_showLoading:
                showLoading();
                break;
            case R.id.btn_pager:
                startActivity(new Intent(this, ScreenSlidePageActivity.class));
                break;
            case R.id.btn_flip:
                startActivity(new Intent(this, CardFlipActivity.class));
                break;
            case R.id.btn_zoom:
                startActivity(new Intent(this, ZoomActivity.class));
                break;
            case R.id.btn_layout:
                startActivity(new Intent(this, LayoutChangesActivity.class));
                break;
        }
    }
}
