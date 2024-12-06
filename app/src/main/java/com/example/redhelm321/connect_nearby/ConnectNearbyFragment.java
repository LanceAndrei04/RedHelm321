package com.example.redhelm321.connect_nearby;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redhelm321.R;
import com.example.redhelm321.SplashActivity;
import com.example.redhelm321.adapters.MessageAdapter;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.models.Message;
import com.example.redhelm321.models.RippleView;
import com.example.redhelm321.profile.UserProfile;
import com.example.redhelm321.utils.PermissionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConnectNearbyFragment extends Fragment {

    private static final String TAG = "ConnectFragment";

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseManager dbManager;

    Button scan_nearby_people, scanButton;
    CardView cardViewAvailableDevices;
    ListView listViewDevices;
    TextView textViewDescription;
    ConstraintLayout chatConstraintLayout, constraintLayoutTitle;
    private RippleView rippleView;

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton btnSend, btnAddFriend, btnDisconnect;
    private MessageAdapter messageAdapter;
    private boolean isDetecting = false;

    ArrayList<String> discoveredDevicesNames;
    ArrayList<WifiP2pDevice> discoveredDevices;
    public ArrayList<String> messages;

    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    WifiP2pManager.PeerListListener peerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;

    PermissionManager permissionManager;
    Server serverClass;
    Client clientClass;

    ArrayAdapter<String> adapter;
    private boolean isHost;
    private String NICKNAME;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        InitializeComponents(rootView);
        InitializeConnectNearbyComponents();
        checkPermissionsForWifiP2p();

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, discoveredDevicesNames);
        listViewDevices.setAdapter(adapter);

        return rootView;
    }

    private void checkPermissionsForWifiP2p() {
        boolean isDeviceTiramisu =
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU;

        if (isDeviceTiramisu) {
            boolean isNearByWifiDevicesPermissionGranted = permissionManager.checkPermission(
                    android.Manifest.permission.NEARBY_WIFI_DEVICES,
                    PermissionManager.REQUEST_CODE_WIFI_P2P);

            if (!isNearByWifiDevicesPermissionGranted) {
                Toast.makeText(getContext(),
                        "Please enable Nearby Wifi Devices permission",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }

        boolean isLocationPermissionGranted = permissionManager.checkPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                PermissionManager.REQUEST_CODE_WIFI_P2P);

        if (!isLocationPermissionGranted) {
            Toast.makeText(getContext(),
                    "Please enable Location permission",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void InitializeConnectNearbyComponents() {
        permissionManager = new PermissionManager(getActivity());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbManager = DatabaseManager.getInstance(mAuth.getCurrentUser().getUid());

        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
//                Toast.makeText(getContext(), "Devices Available", Toast.LENGTH_SHORT).show();
                updateDiscoveredDevices(wifiP2pDeviceList);
            }
        };
        WIFI_P2P_SharedData.setPeerListListener(peerListListener);

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                ReceiveMessageCallback receiveMessageCallback = new ReceiveMessageCallback() {
                    @Override
                    public void updateMessageUI(ChatMessage receivedMessage) {
                        updateUI_OnMessageReceive(receivedMessage);
                    }
                };


                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    updateUI_OnConnect("HOST");
                    isHost = true;

                    if(serverClass == null) {
                        NICKNAME = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                        serverClass = new Server(getActivity(), receiveMessageCallback);
                        serverClass.start();
                    }
                }
                else if (wifiP2pInfo.groupFormed) {
//                    tv_connectionStatus.setText("CONNECTED");
                    updateUI_OnConnect("CLIENT");
                    isHost = false;

                    clientClass = new Client(
                            getActivity(),
                            groupOwnerAddress.getHostAddress(),
                            receiveMessageCallback
                    );
                    clientClass.start();
                }
            }
        };
        WIFI_P2P_SharedData.setConnectionInfoListener(connectionInfoListener);

        discoveredDevices = new ArrayList<>();
        discoveredDevicesNames = new ArrayList<>();
        messages = new ArrayList<>();

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = WIFI_P2P_SharedData.getWifiP2pManager();
        wifiP2pChannel = WIFI_P2P_SharedData.getWifiP2pChannel();
        broadcastReceiver = WIFI_P2P_SharedData.getBroadcastReceiver();
        intentFilter = WIFI_P2P_SharedData.getIntentFilter();
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lv_discoveredDevices_OnItemClick(adapterView, view, i, l);
            }
        });
    }

    private void updateUI_OnMessageReceive(ChatMessage receivedMessage) {

        if (receivedMessage != null) {

            if(receivedMessage.getType().equals(ChatMessage.TYPE_COMMAND_DISCONNECT)) {

                Toast.makeText(getContext(), "DISC", Toast.LENGTH_SHORT).show();
                if(isHost) {
                    disconnectWifiP2p();
                }
                else {
                    restartApp(getContext());
                }
                return;
            }

            if(receivedMessage.getType().equals(ChatMessage.TYPE_COMMAND_FRIEND_REQUEST)) {
                UserProfile.addUserToFriends(dbManager, mAuth.getCurrentUser().getUid(), receivedMessage.getSender());

                Message notification = new Message(receivedMessage.getMessage(), false);
                notification.setNotification(true);
                messageAdapter.addMessage(notification);
                return;
            }

            // Create and add the message
            Message message = new Message(receivedMessage.getMessage(), false);
            messageAdapter.addMessage(message);
            chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void updateUI_OnMessageSend(ChatMessage sentMessage) {
        if (sentMessage != null) {
            // Create and add the message
            if(!sentMessage.getType().equals(ChatMessage.TYPE_MESSAGE)) {
                Message notification = new Message("Friend Request Sent", false);
                notification.setNotification(true);
                messageAdapter.addMessage(notification);
                return;
            }

            Message message = new Message(sentMessage.getMessage(), true);
            messageAdapter.addMessage(message);

            // Clear input and scroll to bottom
            messageInput.setText("");
            chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void updateUI_OnConnect(String role) {
        cardViewAvailableDevices.setVisibility(View.GONE);
        chatConstraintLayout.setVisibility(View.VISIBLE);

        // Add connection notification
        Message notification = new Message("Connected as " + role, false);
        notification.setNotification(true);
        messageAdapter.addMessage(notification);
    }

    private void updateDiscoveredDevices(WifiP2pDeviceList wifiP2pDeviceList) {
//        Toast.makeText(getContext(), "DEVICES AVAILABLE", Toast.LENGTH_SHORT).show();
        if (!wifiP2pDeviceList.getDeviceList().isEmpty() && (serverClass == null && clientClass == null)) {
            rippleView.stopRippleEffect();
            scan_nearby_people.setVisibility(View.GONE);
            textViewDescription.setVisibility(View.GONE);
            constraintLayoutTitle.setVisibility(View.GONE);
            cardViewAvailableDevices.setVisibility(View.VISIBLE);

            discoveredDevices.clear();
            discoveredDevices.addAll(wifiP2pDeviceList.getDeviceList());

            discoveredDevicesNames.clear();

            for (WifiP2pDevice device : discoveredDevices) {
                discoveredDevicesNames.add(device.deviceName);
            }

            adapter.notifyDataSetChanged();

        }
    }

    private void InitializeComponents(View rootView) {
        rippleView = rootView.findViewById(R.id.rippleView);
        cardViewAvailableDevices = rootView.findViewById(R.id.cardViewAvailableDevices);
        listViewDevices = rootView.findViewById(R.id.listViewDevices);
        scanButton = rootView.findViewById(R.id.scanButton);
        textViewDescription =rootView.findViewById(R.id.description);
        scan_nearby_people = rootView.findViewById(R.id.connectButton);
        chatConstraintLayout = rootView.findViewById(R.id.chatConstraintLayout);
        constraintLayoutTitle = rootView.findViewById(R.id.constraintLayoutTitle);

        chatRecyclerView = rootView.findViewById(R.id.chatRecyclerView);
        messageInput = rootView.findViewById(R.id.messageInput);
        btnSend = rootView.findViewById(R.id.btnSend);
        btnAddFriend = rootView.findViewById(R.id.btnScan);
        btnDisconnect = rootView.findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(v -> btnDisconnect_OnClick(v));

        // Setup RecyclerView
        messageAdapter = new MessageAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(messageAdapter);

        // Setup send button click listener
        btnSend.setOnClickListener(v -> sendMessage());

        scan_nearby_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan_nearby_people_OnClick();
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_scan_onClick();
            }
        });

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lv_discoveredDevices_OnItemClick(adapterView, view, i, l);
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFriend_OnClick();
            }
        });
    }

    private void disconnectWifiP2p() {
        wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Stopped Discover", Toast.LENGTH_SHORT).show();

                wifiP2pManager.removeGroup(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        restartApp(getContext());
                    }

                    @Override
                    public void onFailure(int i) {

                    }
                });

            }

            @Override
            public void onFailure(int i) {

            }
        });


    }

    public void restartApp(Context context) {
        Intent intent = new Intent(context, SplashActivity.class); // Main activity of your app
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        System.exit(0); // Forcefully stop the app
    }

    private void btnDisconnect_OnClick(View v) {
        if(isHost) {
            ChatMessage chatMessage = new ChatMessage.Builder(mAuth.getCurrentUser().getUid(), ChatMessage.TYPE_COMMAND_DISCONNECT)
                    .setType(ChatMessage.TYPE_COMMAND_DISCONNECT)
                    .build();

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    serverClass.broadcastMessage(chatMessage);
                }
            });
            disconnectWifiP2p();
            
        }
        else {
            ChatMessage chatMessage = new ChatMessage.Builder(mAuth.getCurrentUser().getUid(), ChatMessage.TYPE_COMMAND_DISCONNECT)
                    .setType(ChatMessage.TYPE_COMMAND_DISCONNECT)
                    .build();

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    clientClass.sendMessage(chatMessage);
                    restartApp(getContext());
                }
            });


        }
    }

    private void btnAddFriend_OnClick() {
        Toast.makeText(getContext(), "ADD FRIEND", Toast.LENGTH_SHORT).show();
        String message = "Friend Request";
        ChatMessage chatMessage = new ChatMessage.Builder(mAuth.getCurrentUser().getUid(), message)
                .setType(ChatMessage.TYPE_COMMAND_FRIEND_REQUEST)
                .build();

        updateUI_OnMessageSend(chatMessage);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(isHost) {
                    serverClass.broadcastMessage(chatMessage);
                }
                else {
                    clientClass.sendMessage(chatMessage);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void lv_discoveredDevices_OnItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final WifiP2pDevice device = discoveredDevices.get(i);
        final WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;

        wifiP2pManager.connect(wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(),
                                "Initiating connection with: " + device.deviceName,
                                Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onFailure(int reason) {
                String errorMessage;
                switch (reason) {
                    case WifiP2pManager.ERROR:
                        errorMessage = "An internal error occurred.";
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        errorMessage = "Wi-Fi Direct is not supported on this device.";
                        break;
                    case WifiP2pManager.BUSY:
                        errorMessage = "The system is busy. Please try again later.";
                        break;
                    case WifiP2pManager.NO_SERVICE_REQUESTS:
                        errorMessage = "No service requests are present.";
                        break;
                    default:
                        errorMessage = "Unknown error occurred.";
                }

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void sendMessage() {
        String message = messageInput.getText().toString();

        if(message.isEmpty()) return;

        ChatMessage chatMessage = new ChatMessage.Builder(mAuth.getCurrentUser().getUid(), message).build();
        updateUI_OnMessageSend(chatMessage);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(isHost) {
                    serverClass.broadcastMessage(chatMessage);
                }
                else {
                    clientClass.sendMessage(chatMessage);
                }
            }
        });
    }

    private void cancel_scan_onClick() {
        rippleView.stopRippleEffect();
        scan_nearby_people.setVisibility(View.GONE);
        textViewDescription.setVisibility(View.GONE);
        constraintLayoutTitle.setVisibility(View.GONE);
        cardViewAvailableDevices.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        checkPermissionsForWifiP2p();
    }

    @SuppressLint("MissingPermission")
    private void scan_nearby_people_OnClick() {
        if (isDetecting) {
            rippleView.stopRippleEffect();
            wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i) {

                }
            });
        } else {
            rippleView.startRippleEffect(scan_nearby_people.getWidth());


            wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Discovering devices", Toast.LENGTH_SHORT).show();
//                    tv_connectionStatus.setText("Discovering devices");
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(getContext(), "Failed to discover devices" + i, Toast.LENGTH_SHORT).show();
//                    tv_connectionStatus.setText("Failed to discover devices");
                }
            });

        }
        isDetecting = !isDetecting;
    }
}