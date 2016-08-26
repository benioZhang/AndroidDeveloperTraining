package com.benio.training.class16;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benio.training.R;

public class Class16Activity extends AppCompatActivity {
    private static final String TAG = "Class16Activity";
    private Scene mSceneOne;
    private Scene mSceneTwo;
    private ViewGroup mSceneRoot;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class16);
        // Create the scene root for the scenes in this app
        mSceneRoot = (ViewGroup) findViewById(R.id.scene_root);
        mTextView = (TextView) findViewById(R.id.title);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createScene() {
        // Create the scenes
        mSceneOne = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_one, this);
        mSceneTwo = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_two, this);

        // create scene in code
        ViewGroup viewHierarchy = (ViewGroup) findViewById(R.id.scene_root);
        Scene scene = new Scene(mSceneRoot, viewHierarchy);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createTransition() {
        // Create transition from resource
        Transition transition1 = TransitionInflater.from(this)
                .inflateTransition(R.transition.fade_transition);
        Transition transitionSet = TransitionInflater.from(this)
                .inflateTransition(R.transition.auto_transition);
        // Create transition in code
        Transition fadeTransition = new Fade();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void applyTransition(View view) {
        Transition transition = new Fade();
        mSceneTwo = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_two, this);
        TransitionManager.go(mSceneTwo, transition);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void chooseTargetView() {
        Transition transition = new Fade();
        transition.addTarget(mTextView);
        transition.removeTarget(mTextView);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void applyTransitionWithoutScenes(View view) {
        // Create a new TextView and set some View properties
        TextView textView = new TextView(this);
        textView.setText("new Text Label");
        textView.setTextSize(20);

        Transition fade = new Fade(Fade.IN);
        fade.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(TAG, "onTransitionStart: ");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d(TAG, "onTransitionEnd: ");
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d(TAG, "onTransitionCancel: ");
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d(TAG, "onTransitionPause: ");
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d(TAG, "onTransitionResume: ");
            }
        });
        // Start recording changes to the view hierarchy
        TransitionManager.beginDelayedTransition(mSceneRoot, fade);
        // Add the new TextView to the view hierarchy
        mSceneRoot.addView(textView);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void applyCustomTransition(View view) {
        Transition transition = new CustomTransition();
        Scene scene = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_one, this);
        TransitionManager.go(scene, transition);
    }
}
