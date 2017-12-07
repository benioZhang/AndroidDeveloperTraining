package com.benio.training.class18;

import android.annotation.TargetApi;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.benio.training.R;

import java.util.Map;

public class P2pServiceDiscoveryActivity extends AppCompatActivity {
    private static final String TAG = "P2pServiceDiscovery";
    private static final int SERVER_PORT = 8000;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    final Map<String, String> buddies = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_service_discovery);
        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void startRegistration(View view) {
        Map<String, String> record = new ArrayMap<>();
        //  Create a string map containing information about your service.
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test",
                "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d(TAG, "add localService success");
            }

            @Override
            public void onFailure(int reason) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "add localService error, reason: " + reason);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void discoveryService(View view) {
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            /** Callback includes:
             * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
             * record: TXT record dta as a map of key/value pairs.
             * device: The device running the advertised service.
             */
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName,
                                                  Map<String, String> txtRecordMap,
                                                  WifiP2pDevice srcDevice) {
                Log.d(TAG, "DnsSdTxtRecord available: " + txtRecordMap.toString());
                buddies.put(srcDevice.deviceAddress, txtRecordMap.get("buddyName"));
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener responseListener =
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType,
                                                        WifiP2pDevice srcDevice) {
                        // Update the device name with the human-friendly version from
                        // the DnsTxtRecord, assuming one arrived.
                        srcDevice.deviceName = buddies.containsKey(srcDevice.deviceAddress) ?
                                buddies.get(srcDevice.deviceAddress) : srcDevice.deviceName;

                        // Add to the custom adapter defined specifically for showing
                        // wifi devices.
//                        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
//                                .findFragmentById(R.id.frag_peerlist);
//                        WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
//                                .getListAdapter());
//
//                        adapter.add(resourceType);
//                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
                    }
                };
        mManager.setDnsSdResponseListeners(mChannel, responseListener, txtRecordListener);

        WifiP2pServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "add service request success");
            }

            @Override
            public void onFailure(int reason) {
                // Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "add service request error, reason: " + reason);
            }
        });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "discoverServices success ");
            }

            @Override
            public void onFailure(int reason) {
                // Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "discoverServices error, reason: " + reason);
            }
        });
    }
}
