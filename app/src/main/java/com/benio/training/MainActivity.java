package com.benio.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.benio.training.class1.Class1Activity;
import com.benio.training.class10.Class10Activity;
import com.benio.training.class11.Class11Activity;
import com.benio.training.class12.Class12Activity;
import com.benio.training.class13.Class13Activity;
import com.benio.training.class14.Class14Activity;
import com.benio.training.class15.Class15Activity;
import com.benio.training.class16.Class16Activity;
import com.benio.training.class2.Class2Activity;
import com.benio.training.class3.Class3Activity;
import com.benio.training.class4.Class4Activity;
import com.benio.training.class5.Class5Activity;
import com.benio.training.class6.Class6Activity;
import com.benio.training.class7.Class7Activity;
import com.benio.training.class8.Class8Activity;
import com.benio.training.class9.Class9Activity;

public class MainActivity extends AppCompatActivity {
    DemoInfo[] mInfos = {
            new DemoInfo("Building Your First App", Class1Activity.class),
            new DemoInfo("Building Your First App(2)", Class2Activity.class),
            new DemoInfo("Managing the Activity Lifecycle", Class3Activity.class),
            new DemoInfo("Building a Dynamic UI with Fragments", Class4Activity.class),
            new DemoInfo("Saving Data", Class5Activity.class),
            new DemoInfo("Interacting with Other Apps", Class6Activity.class),
            new DemoInfo("Working with System Permissions", Class7Activity.class),
            new DemoInfo("Sharing Simple Data", Class8Activity.class),
            new DemoInfo("Setting Up File Sharing", Class9Activity.class),
            new DemoInfo("Sharing Files with NFC", Class10Activity.class),
            new DemoInfo("Managing Audio Playback", Class11Activity.class),
            new DemoInfo("Capturing Photos", Class12Activity.class),
            new DemoInfo("Printing Content", Class13Activity.class),
            new DemoInfo("Displaying Bitmaps Efficiently", Class14Activity.class),
            new DemoInfo("Displaying Graphics with OpenGL ES", Class15Activity.class),
            new DemoInfo("Animating Views Using Scenes and Transitions", Class16Activity.class)
    };

    private static class DemoInfo {
        String title;
        Class<?> activityClass;

        public DemoInfo(String title, Class<?> activityClass) {
            this.title = title;
            this.activityClass = activityClass;
        }
    }

    private static class MyAdapter extends BaseAdapter {
        private DemoInfo[] mInfos;

        public MyAdapter(DemoInfo[] infos) {
            mInfos = infos;
        }

        @Override
        public int getCount() {
            return mInfos.length;
        }

        @Override
        public Object getItem(int position) {
            return mInfos[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
            }
            textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(mInfos[position].title);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list);
        final BaseAdapter adapter = new MyAdapter(mInfos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DemoInfo demoInfo = (DemoInfo) adapter.getItem(position);
                startActivity(new Intent(MainActivity.this, demoInfo.activityClass));
            }
        });
    }
}
