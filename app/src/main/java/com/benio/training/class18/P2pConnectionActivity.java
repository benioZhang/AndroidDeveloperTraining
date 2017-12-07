package com.benio.training.class18;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.benio.training.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhangzhibin on 2017/12/6.
 */
public class P2pConnectionActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {
    private static final String TAG = "MyWifiActivity";

    private IntentFilter mIntentFilter = new IntentFilter();
    private BroadcastReceiver mBroadcastReceiver;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> mWifiP2pDevices = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_connection);

        // Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        WifiP2pManager manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
        mBroadcastReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        mManager = manager;
        mChannel = channel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void setIsWifiP2pEnabled(boolean enabled) {
        Log.d(TAG, enabled ? "Wifi P2p is enabled" : "Wifi P2p is disabled");
    }

    public void requestGroupInfo(View view) {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    String passphrase = group.getPassphrase();
                    Log.i(TAG, "onGroupInfoAvailable, passphrase: " + passphrase);
                }
            }
        });
    }

    public void createGroup(View view) {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Create group failed, reason = [" + reason + "]");
            }
        });
    }

    public void connect(View view) {
        if (mWifiP2pDevices.isEmpty()) {
            Log.d(TAG, "No devices");
            return;
        }
        // Picking the first device found on the network.
        WifiP2pDevice device = mWifiP2pDevices.get(0);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry. Reason: " + reason);
            }
        });
    }

    public void findPeers(View view) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
                Log.d(TAG, "discover peers success");
            }

            @Override
            public void onFailure(int reason) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Log.d(TAG, "discover peers error: " + reason);
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Collection<WifiP2pDevice> refreshedPeers = peers.getDeviceList();
        if (!mWifiP2pDevices.equals(refreshedPeers)) {
            mWifiP2pDevices.clear();
            mWifiP2pDevices.addAll(refreshedPeers);

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        if (mWifiP2pDevices.isEmpty()) {
            Log.d(TAG, "No devices found");
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress;
        // After the group negotiation, we can determine the group owner
        // (server).

        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }
}
