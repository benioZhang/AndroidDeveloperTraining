package com.benio.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.benio.training.class1.Class1Activity;
import com.benio.training.class10.Class10Activity;
import com.benio.training.class11.Class11Activity;
import com.benio.training.class12.Class12Activity;
import com.benio.training.class13.Class13Activity;
import com.benio.training.class14.Class14Activity;
import com.benio.training.class15.Class15Activity;
import com.benio.training.class2.Class2Activity;
import com.benio.training.class3.Class3Activity;
import com.benio.training.class4.Class4Activity;
import com.benio.training.class5.Class5Activity;
import com.benio.training.class6.Class6Activity;
import com.benio.training.class7.Class7Activity;
import com.benio.training.class8.Class8Activity;
import com.benio.training.class9.Class9Activity;

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
        findViewById(R.id.btn_class7).setOnClickListener(this);
        findViewById(R.id.btn_class8).setOnClickListener(this);
        findViewById(R.id.btn_class9).setOnClickListener(this);
        findViewById(R.id.btn_class10).setOnClickListener(this);
        findViewById(R.id.btn_class11).setOnClickListener(this);
        findViewById(R.id.btn_class12).setOnClickListener(this);
        findViewById(R.id.btn_class13).setOnClickListener(this);
        findViewById(R.id.btn_class14).setOnClickListener(this);
        findViewById(R.id.btn_class15).setOnClickListener(this);
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

            case R.id.btn_class7:
                startActivity(new Intent(this, Class7Activity.class));
                break;

            case R.id.btn_class8:
                startActivity(new Intent(this, Class8Activity.class));
                break;

            case R.id.btn_class9:
                startActivity(new Intent(this, Class9Activity.class));
                break;

            case R.id.btn_class10:
                startActivity(new Intent(this, Class10Activity.class));
                break;

            case R.id.btn_class11:
                startActivity(new Intent(this, Class11Activity.class));
                break;

            case R.id.btn_class12:
                startActivity(new Intent(this, Class12Activity.class));
                break;

            case R.id.btn_class13:
                startActivity(new Intent(this, Class13Activity.class));
                break;

            case R.id.btn_class14:
                startActivity(new Intent(this, Class14Activity.class));
                break;

            case R.id.btn_class15:
                startActivity(new Intent(this, Class15Activity.class));
                break;

            default:
                break;
        }
    }
}
