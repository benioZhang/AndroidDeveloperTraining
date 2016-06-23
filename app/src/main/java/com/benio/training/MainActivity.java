package com.benio.training;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.benio.training.class1.MyActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_class1).setOnClickListener(this);
        findViewById(R.id.btn_class2).setOnClickListener(this);
        findViewById(R.id.btn_class3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_class1:
                startActivity(new Intent(this, MyActivity.class));
                break;

            case R.id.btn_class2:
                break;

            case R.id.btn_class3:
                break;

            default:
                break;
        }
    }
}
