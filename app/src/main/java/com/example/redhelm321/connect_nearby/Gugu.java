//
//package com.appdev.redhelm321.chat_room;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.wifi.WifiManager;
//import android.net.wifi.p2p.WifiP2pConfig;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pDeviceList;
//import android.net.wifi.p2p.WifiP2pGroup;
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.appdev.redhelm321.MainActivity;
//import com.appdev.redhelm321.R;
//import com.appdev.redhelm321.utils.FirebaseAuthUtils;
//import com.appdev.redhelm321.utils.PermissionManager;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class ConnectNearbyActivity extends AppCompatActivity {
//
//    ArrayList<String> discoveredDevicesNames;
//    ArrayList<WifiP2pDevice> discoveredDevices;
//    public ArrayList<String> messages;
//
//    ListView lv_discoveredDeviceList;
//    public ListView lv_messageList;
//    Button btn_scanDevices;
//    Button btn_group;
//    TextView tv_connectionStatus;
//    Button btn_sendMessage;
//    EditText et_messageInput;
//
//    BroadcastReceiver broadcastReceiver;
//    IntentFilter intentFilter;
//    WifiManager wifiManager;
//    WifiP2pManager wifiP2pManager;
//    WifiP2pManager.Channel wifiP2pChannel;
//    WifiP2pManager.PeerListListener peerListListener;
//    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
//
//    PermissionManager permissionManager;
//    Server serverClass;
//    Client clientClass;
//
//    private FirebaseAuth mAuth;
//    private FirebaseAuthUtils firebaseAuthUtils;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference FBDB_profilesRef;
//
//    boolean isHost;
//
//    static final int MESSAGE_READ = 1;
//    private String nickname;
//    private String NICKNAME;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_connect_nearby);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//        InitializeComponents();
//        checkPermissionsForWifiP2p();
//    }
//
//
//
//    private void InitializeComponents() {
//        permissionManager = new PermissionManager(this);
//
//        peerListListener = new WifiP2pManager.PeerListListener() {
//            @Override
//            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
//                Toast.makeText(ConnectNearbyActivity.this, "Devices Available", Toast.LENGTH_SHORT).show();
//                updateDiscoveredDevices(wifiP2pDeviceList);
//            }
//        };
//        WIFI_P2P_SharedData.setPeerListListener(peerListListener);
//
//        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
//            @Override
//            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
//                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
//
//                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
//                    Toast.makeText(ConnectNearbyActivity.this, "A server has made connection2P", Toast.LENGTH_SHORT).show();
//
//                    tv_connectionStatus.setText("HOST");
//                    isHost = true;
//
//                    if(serverClass == null) {
//                        NICKNAME = wifiP2pInfo.groupOwnerAddress.getHostAddress();
//                        serverClass = new Server(
//                                ConnectNearbyActivity.this,
//                                lv_messageList,
//                                messages);
//                        serverClass.start();
//                    }
//                }
//                else if (wifiP2pInfo.groupFormed) {
//                    tv_connectionStatus.setText("CONNECTED");
//                    isHost = false;
//                    clientClass = new Client(
//                            ConnectNearbyActivity.this,
//                            groupOwnerAddress.getHostAddress(),
//                            lv_messageList,
//                            messages,
//                            mAuth.getCurrentUser().getDisplayName());
//                    clientClass.start();
//                }
//            }
//        };
//        WIFI_P2P_SharedData.setConnectionInfoListener(connectionInfoListener);
//
//        discoveredDevices = new ArrayList<>();
//        discoveredDevicesNames = new ArrayList<>();
//        messages = new ArrayList<>();
//
//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiP2pManager = WIFI_P2P_SharedData.getWifiP2pManager();
//        wifiP2pChannel = WIFI_P2P_SharedData.getWifiP2pChannel();
//        broadcastReceiver = WIFI_P2P_SharedData.getBroadcastReceiver();
//        intentFilter = WIFI_P2P_SharedData.getIntentFilter();
//        lv_discoveredDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                lv_discoveredDevices_OnItemClick(adapterView, view, i, l);
//            }
//        });
//
//
//
//
//        tv_connectionStatus = findViewById(R.id.tv_connectionStatus);
//    }
//
//    @SuppressLint("MissingPermission")
//    private void btn_group_OnClick() {
//        wifiP2pManager.requestGroupInfo(wifiP2pChannel, new WifiP2pManager.GroupInfoListener() {
//            @Override
//            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
//                Log.d("P2PINFO", wifiP2pGroup.getNetworkName());
//                for (WifiP2pDevice device : wifiP2pGroup.getClientList()) {
//                    Log.d("P2PINFO", device.deviceName);
//                }
//                Log.d("P2PINFO", wifiP2pGroup.getInterface());
//            }
//        });
//    }
//
//    @SuppressLint("MissingPermission") // Ensured that the permission was already granted
//    private void lv_discoveredDevices_OnItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        final WifiP2pDevice device = discoveredDevices.get(i);
//        final WifiP2pConfig config = new WifiP2pConfig();
//
////        device.isGroupOwner()
//
//        config.deviceAddress = device.deviceAddress;
//
//        wifiP2pManager.connect(wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(ConnectNearbyActivity.this,
//                                "Connected to: " + device.deviceName,
//                                Toast.LENGTH_SHORT)
//                        .show();
//
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                String errorMessage;
//                switch (reason) {
//                    case WifiP2pManager.ERROR:
//                        errorMessage = "An internal error occurred.";
//                        break;
//                    case WifiP2pManager.P2P_UNSUPPORTED:
//                        errorMessage = "Wi-Fi Direct is not supported on this device.";
//                        break;
//                    case WifiP2pManager.BUSY:
//                        errorMessage = "The system is busy. Please try again later.";
//                        break;
//                    case WifiP2pManager.NO_SERVICE_REQUESTS:
//                        errorMessage = "No service requests are present.";
//                        break;
//                    default:
//                        errorMessage = "Unknown error occurred.";
//                }
//
//                Toast.makeText(ConnectNearbyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
//
//    // event triggers/handlers
//    @SuppressLint("MissingPermission")
//    private void startDeviceScan() {
//        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                Toast.makeText(ConnectNearbyActivity.this, "Discovering devices", Toast.LENGTH_SHORT).show();
//                tv_connectionStatus.setText("Discovering devices");
//            }
//
//            @Override
//            public void onFailure(int i) {
//                Toast.makeText(ConnectNearbyActivity.this, "Failed to discover devices" + i, Toast.LENGTH_SHORT).show();
//                tv_connectionStatus.setText("Failed to discover devices");
//            }
//        });
//    }
//
//    private void btn_sendMessage_OnClick() {
//        String message = et_messageInput.getText().toString();
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if(isHost) {
//                    serverClass.broadcastMessage("[H]"+ mAuth.getCurrentUser().getDisplayName() + ": " + message);
//                }
//                else {
//                    clientClass.sendMessage(message);
//                }
//            }
//        });
//    }
//
//    private void btn_scanDevices_OnClick() {
//        startDeviceScan();
//    }
//
//    private void checkPermissionsForWifiP2p() {
//        boolean isDeviceTiramisu =
//                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU;
//
//        if (isDeviceTiramisu) {
//            boolean isNearByWifiDevicesPermissionGranted = permissionManager.checkPermission(
//                    android.Manifest.permission.NEARBY_WIFI_DEVICES,
//                    PermissionManager.REQUEST_CODE_WIFI_P2P);
//
//            if (!isNearByWifiDevicesPermissionGranted) {
//                Toast.makeText(this,
//                        "Please enable Nearby Wifi Devices permission",
//                        Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//
//        boolean isLocationPermissionGranted = permissionManager.checkPermission(
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                PermissionManager.REQUEST_CODE_WIFI_P2P);
//
//        if (!isLocationPermissionGranted) {
//            Toast.makeText(this,
//                    "Please enable Location permission",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    private void updateDiscoveredDevices(WifiP2pDeviceList wifiP2pDeviceList) {
//        if (!wifiP2pDeviceList.getDeviceList().isEmpty()) {
//            tv_connectionStatus.setText("Device(s) found");
//            discoveredDevices.clear();
//            discoveredDevices.addAll(wifiP2pDeviceList.getDeviceList());
//
//            discoveredDevicesNames.clear();
//
//            for (WifiP2pDevice device : discoveredDevices) {
//                discoveredDevicesNames.add(device.deviceName);
//            }
//
//            lv_discoveredDeviceList.setAdapter(new ArrayAdapter<String>(
//                    ConnectNearbyActivity.this,
//                    android.R.layout.simple_list_item_1,
//                    discoveredDevicesNames)
//            );
//        }
//        else {
//            Toast.makeText(ConnectNearbyActivity.this, "No devices found", Toast.LENGTH_SHORT).show();
//            tv_connectionStatus.setText("No devices found");
//        }
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PermissionManager.REQUEST_CODE_WIFI_P2P) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                checkPermissionsForWifiP2p();
//            } else {
//                permissionManager.redirectToSettings();
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(broadcastReceiver, intentFilter);
//        checkPermissionsForWifiP2p();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//
//    }
//
//
//}*/
