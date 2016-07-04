package com.benio.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.benio.training.class1.Class1Activity;
import com.benio.training.class2.Class2Activity;
import com.benio.training.class3.Class3Activity;
import com.benio.training.class4.Class4Activity;
import com.benio.training.class5.Class5Activity;
import com.benio.training.class6.Class6Activity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_class1).setOnClickListener(this);
        findViewById(R.id.btn_class2).setOnClickListener(this);
        findViewById(R.id.btn_class3).setOnClickListener(this);
        findViewById(R.id.btn_class4).setOnClickListener(this);
        findViewById(R.id.btn_class5).setOnClickListener(this);
        findViewById(R.id.btn_class6).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_class1:
                startActivity(new Intent(this, Class1Activity.class));
                break;

            case R.id.btn_class2:
                startActivity(new Intent(this, Class2Activity.class));
                break;

            case R.id.btn_class3:
                startActivity(new Intent(this, Class3Activity.class));
                break;

            case R.id.btn_class4:
                startActivity(new Intent(this, Class4Activity.class));
                break;

            case R.id.btn_class5:
                startActivity(new Intent(this, Class5Activity.class));
                break;

            case R.id.btn_class6:
                startActivity(new Intent(this, Class6Activity.class));
                break;

            default:
                break;
        }
    }
}
