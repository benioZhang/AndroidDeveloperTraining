package com.benio.training.class18;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.benio.training.R;

public class Class18Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class18);
        findViewById(R.id.btn_nsd).setOnClickListener(this);
        findViewById(R.id.btn_p2p).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nsd:
                startActivity(new Intent(this, NsdChatActivity.class));
                break;
            case R.id.btn_p2p:
                startActivity(new Intent(this, P2pConnectionActivity.class));
                break;
        }
    }
}
