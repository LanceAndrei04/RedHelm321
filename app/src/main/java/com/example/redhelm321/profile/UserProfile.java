package com.example.redhelm321.profile;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.redhelm321.database.DatabaseCallback;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.database.ReadCallback;

import java.util.ArrayList;

public class UserProfile {
    public static final String DEFAULT_PROFILE_PIC = "https://i.imgur.com/ACeuiSf.png";
    private String userImgLink;
    private String name;
    private int age;
    private String birthDate;
    private String address;
    private String phoneNumber;
    private String bloodType;
    private String email;
    private String status;
    private String report;
    private String latestTimeStatusUpdate;
    private String location;
    private ArrayList<String> friendIDList;


    // Default no-argument constructor required by Firebase
    public UserProfile() {
        this.friendIDList = new ArrayList<>();
    }

    // Constructor for manual instantiation
    public UserProfile(
            String userImgLink, String name,
            int age, String birthDate,
            String address, String phoneNumber,
            String bloodType, String email,
            String status, String latestTimeStatusUpdate,
            String report, String location, ArrayList<String> friendIDList) {

        this.userImgLink = userImgLink;
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.email = email;
        this.status = status;
        this.latestTimeStatusUpdate = latestTimeStatusUpdate;
        this.report = report;
        this.location = location;
        this.friendIDList = friendIDList;

    }

    // Method to set image to an ImageView using Glide
    public static void setImageToImageView(Context context, ImageView imageView, String userImgLink) {
        Glide.with(context)
                .load(userImgLink)
                .into(imageView); // Target ImageView
    }

    public static void addUserToFriends(DatabaseManager dbManager, String userId, String friendId) {
        Log.d("DEBUG_USERFRIENDS", "Friend ID: " + friendId);

        String userFriendsPath = "profiles/" + userId + "/friendIDList";

        dbManager.readData(userFriendsPath, Object.class, new ReadCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                // Ensure the data is cast to ArrayList<String> if not null
                ArrayList<String> friends = (data instanceof ArrayList) ? (ArrayList<String>) data : new ArrayList<>();

                // Avoid duplicates
                if (!friends.contains(friendId)) {
                    friends.add(friendId);

                    // Save updated friend list
                    dbManager.saveData(userFriendsPath, friends, new DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("DEBUG_USERFRIENDS", "Friend added successfully.");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("DEBUG_USERFRIENDS", "Failed to save friend: " + e.getMessage(), e);
                        }
                    });

                    addUserToFriends(dbManager, friendId, userId);
                } else {
                    Log.d("DEBUG_USERFRIENDS", "Friend ID already exists in the list.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DEBUG_USERFRIENDS", "Failed to read friends list: " + e.getMessage(), e);
            }
        });
    }

    public static void getUserFriends(DatabaseManager dbManager, String userId, ReadCallback<ArrayList<String>> callback) {
        Log.d("DEBUG_USERFRIENDS", "Fetching friends for User ID: " + userId);

        String userFriendsPath = "profiles/" + userId + "/friendIDList";

        dbManager.readData(userFriendsPath, Object.class, new ReadCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                // Ensure the data is cast to ArrayList<String> if not null
                ArrayList<String> friends = (data instanceof ArrayList) ? (ArrayList<String>) data : new ArrayList<>();

                // Pass the friends list to the provided callback
                callback.onSuccess(friends);
                Log.d("DEBUG_USERFRIENDS", "Friends list retrieved successfully.");
            }

            @Override
            public void onFailure(Exception e) {
                // Pass the failure to the provided callback
                callback.onFailure(e);
                Log.e("DEBUG_USERFRIENDS", "Failed to fetch friends list: " + e.getMessage(), e);
            }
        });
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatestTimeStatusUpdate() {
        return latestTimeStatusUpdate;
    }

    public void setLatestTimeStatusUpdate(String latestTimeStatusUpdate) {
        this.latestTimeStatusUpdate = latestTimeStatusUpdate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getters and Setters (Firebase needs these or public fields)
    public String getUserImgLink() {
        return userImgLink;
    }

    public void setUserImgLink(String userImgLink) {
        this.userImgLink = userImgLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and Setters for friendIDList
    public ArrayList<String> getFriendIDList() {
        return friendIDList;
    }

    public void setFriendIDList(ArrayList<String> friendIDList) {
        this.friendIDList = friendIDList;
    }

    // Static Builder Class for manual creation
    public static class Builder {
        private String userImgLink;
        private String name;
        private int age;
        private String birthDate;
        private String address;
        private String phoneNumber;
        private String bloodType;
        private String email;
        private String status;
        private String report;
        private String latestTimeStatusUpdate;
        private String location;
        private ArrayList<String> friendIDList;

        public Builder setLatestTimeStatusUpdate(String latestTimeStatusUpdate) {
            this.latestTimeStatusUpdate = latestTimeStatusUpdate;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setReport(String report) {
            this.report = report;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setUserImgLink(String userImgLink) {
            this.userImgLink = userImgLink;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setBirthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setBloodType(String bloodType) {
            this.bloodType = bloodType;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setFriendIDList(ArrayList<String> friendIDList) {
            this.friendIDList = friendIDList;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(
                    userImgLink, name, age, birthDate,
                    address, phoneNumber, bloodType, email,
                    status, latestTimeStatusUpdate, report, location, friendIDList);
        }
    }
}