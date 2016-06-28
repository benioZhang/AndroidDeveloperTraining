package com.benio.training.class3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.benio.training.R;

public class Class3Activity extends AppCompatActivity {
    private static final String TAG = "Class3Activity";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class3);

        mTextView = (TextView) findViewById(R.id.text_message);
        mTextView.append("onCreate\n");
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTextView.append("onStart\n");
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextView.append("onResume\n");
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTextView.append("onPause\n");
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTextView.append("onStop\n");
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTextView.append("onRestart\n");
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTextView.append("onDestroy\n");
        Log.d(TAG, "onDestroy: ");
    }

    public void openDialog(View view) {
        startActivity(new Intent(this, DialogActivity.class));
    }
}
