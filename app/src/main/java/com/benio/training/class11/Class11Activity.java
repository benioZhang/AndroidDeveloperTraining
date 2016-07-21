package com.benio.training.class11;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.benio.training.R;

public class Class11Activity extends AppCompatActivity {
    private static final String TAG = "Class11Activity";
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                Log.d(TAG, "Pause playback");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                Log.d(TAG, "Resume playback");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
                mAudioManager.abandonAudioFocus(mFocusChangeListener);
                // Stop playback
                Log.d(TAG, "Stop playback");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
                Log.d(TAG, "Lower the volume");
            }
            Log.d(TAG, "onAudioFocusChange: " + focusChange);
        }
    };
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class11);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(this, RemoteControlReceiver.class);
        startPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterMediaReceiver(null);
        stopPlayback();
    }

    public void registerMediaReceiver(View view) {
        // Start listening for button presses
        mAudioManager.registerMediaButtonEventReceiver(mComponentName);
    }

    public void unregisterMediaReceiver(View view) {
        // Stop listening for button presses
        mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
    }

    public void requestFocus(View view) {
        // Request audio focus for playback
        int result = mAudioManager.requestAudioFocus(mFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
                // Request transient focus.
                // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
            // Start playback.
            showToast("Radio focus is granted.");
        }
    }

    public void abandonFocus(View view) {
        // Abandon audio focus when playback complete.
        mAudioManager.abandonAudioFocus(mFocusChangeListener);
    }

    public void checkHardware(View view) {
        if (mAudioManager.isBluetoothA2dpOn()) {
            showToast("Bluetooth headset is on");
            // Adjust output for bluetooth.
        } else if (mAudioManager.isSpeakerphoneOn()) {
            showToast("Speakerphone is on");
            // Adjust output for speakerphone.
        } else if (mAudioManager.isWiredHeadsetOn()) {
            showToast("WiredHeadset is on");
            // Adjust output for headsets.
        } else {
            Log.d(TAG, "checkHardware: ");
            // If audio plays and no one can hear it, is it still playing?
        }
    }

    private void showToast(CharSequence msg) {
        Toast.makeText(Class11Activity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver mNoisyAudioStreamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Pause the playback
            Log.d(TAG, "NoisyAudioStreamReceiver: " + intent.getAction());
        }
    };

    private void startPlayback() {
        registerReceiver(mNoisyAudioStreamReceiver,
                new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    private void stopPlayback() {
        unregisterReceiver(mNoisyAudioStreamReceiver);
    }
}
