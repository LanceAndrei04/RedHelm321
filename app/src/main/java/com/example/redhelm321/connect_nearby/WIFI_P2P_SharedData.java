package com.example.redhelm321.connect_nearby;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

public class WIFI_P2P_SharedData {
    private static WifiP2pManager.PeerListListener peerListListener;
    private static WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private static WifiManager wifiManager;
    private static WifiP2pManager wifiP2pManager;
    private static WifiP2pManager.Channel wifiP2pChannel;
    private static BroadcastReceiver broadcastReceiver;
    private static IntentFilter intentFilter;


    public static WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
        return connectionInfoListener;
    }

    public static void setConnectionInfoListener(WifiP2pManager.ConnectionInfoListener obj) {
        connectionInfoListener = obj;
    }

    public static IntentFilter getIntentFilter() {
        return intentFilter;
    }
    public static void setIntentFilter(IntentFilter obj) {
        intentFilter = obj;
    }

    public static BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

    public static void setBroadcastReceiver(BroadcastReceiver obj) {
        broadcastReceiver = obj;
    }

    public static WifiP2pManager.PeerListListener getPeerListListener() {
        return peerListListener;
    }

    public static void setPeerListListener(WifiP2pManager.PeerListListener obj) {
        peerListListener = obj;
    }

    public static WifiP2pManager.Channel getWifiP2pChannel() {
        return wifiP2pChannel;
    }

    public static void setWifiP2pChannel(WifiP2pManager.Channel obj) {
        wifiP2pChannel = obj;
    }

    public static WifiP2pManager getWifiP2pManager() {
        return wifiP2pManager;
    }

    public static void setWifiP2pManager(WifiP2pManager obj) {
        wifiP2pManager = obj;
    }

    public static WifiManager getWifiManager() {
        return wifiManager;
    }

    public static void setWifiManager(WifiManager obj) {
        wifiManager = obj;
    }


}