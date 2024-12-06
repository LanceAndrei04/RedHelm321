package com.example.redhelm321.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionManager {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_PERMISSION_ASKED = "permission_asked";
    public static final int REQUEST_CODE_WIFI_P2P = 100;

    private final SharedPreferences prefs;

    private final FragmentActivity context;

    public PermissionManager(FragmentActivity context) {
        this.context = context;
        prefs = this.context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTimeAskingPermission() {
        return !prefs.getBoolean(KEY_PERMISSION_ASKED, false);
    }

    public void markPermissionAsked() {
        prefs.edit().putBoolean(KEY_PERMISSION_ASKED, true).apply();
    }

    public boolean checkPermission(String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            // First time asking or "Don't ask again" selected
            if (isFirstTimeAskingPermission()) {
                // Ask for permission
                ActivityCompat.requestPermissions(context, new String[]{permission}, REQUEST_CODE);
                markPermissionAsked();
            } else {
                // Redirect to settings
                redirectToSettings();
            }
            return false;
        }
        return true;
    }

    public void redirectToSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void showPermissionRationale(String permission, int REQUEST_CODE) {
        ActivityCompat.requestPermissions(
                context,
                new String[]{permission}, // Replace YOUR_PERMISSION with actual permission
                REQUEST_CODE
        );

    }
}