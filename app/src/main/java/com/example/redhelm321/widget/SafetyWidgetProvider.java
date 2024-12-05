package com.example.redhelm321.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.redhelm321.MainActivity;
import com.example.redhelm321.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SafetyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Create RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_safety_status);

        // Set up click intent for the SOS button
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_sos_button, pendingIntent);

        // Get current user's status if logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("profiles")
                    .child(currentUser.getUid());
            
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String status = dataSnapshot.child("status").getValue(String.class);
                        if (status != null) {
                            views.setTextViewText(R.id.widget_status, "Status: " + status);
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    views.setTextViewText(R.id.widget_status, "Status: Unknown");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            });
        } else {
            views.setTextViewText(R.id.widget_status, "Please login");
        }

        // Update the widget immediately with initial state
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Widget was added to the home screen
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Widget was removed from the home screen
    }
}
