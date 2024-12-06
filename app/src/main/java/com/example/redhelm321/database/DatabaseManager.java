package com.example.redhelm321.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final DatabaseReference databaseReference;

    public final static String PROFILE_PATH = "profiles";

    private String userId;

    // Private constructor for Singleton
    private DatabaseManager(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        this.userId = userId;
    }

    // Get the singleton instance
    public static synchronized DatabaseManager getInstance(String userId) {
        if (instance == null) {
            instance = new DatabaseManager(userId);
        }
        return instance;
    }

    public String getUserProfilePath(String userID) {
        return PROFILE_PATH + "/" + userID;
    }

    // Save data to a specific path
    public <T> void saveData(String path, T data, DatabaseCallback callback) {
        databaseReference.child(path).setValue(data)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Read data from a specific path
    public <T> void readData(String path, Class<T> clazz, ReadCallback<T> callback) {
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T data = snapshot.getValue(clazz);
                    callback.onSuccess(data);
                } else {
                    callback.onFailure(new Exception("Data not found at path: " + path));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }


}
