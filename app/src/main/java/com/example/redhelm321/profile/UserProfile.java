package com.example.redhelm321.profile;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
    private String latestTimeStatusUpdate;

    // Default no-argument constructor required by Firebase
    public UserProfile() {
    }

    // Constructor for manual instantiation
    public UserProfile(String userImgLink, String name, int age, String birthDate, String address, String phoneNumber, String bloodType, String email, String status, String latestTimeStatusUpdate) {
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

    }

    // Method to set image to an ImageView using Glide
    public static void setImageToImageView(Context context, ImageView imageView, String userImgLink) {
        Glide.with(context)
                .load(userImgLink)
                .into(imageView); // Target ImageView
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
        private String latestTimeStatusUpdate;

        public Builder setLatestTimeStatusUpdate(String latestTimeStatusUpdate) {
            this.latestTimeStatusUpdate = latestTimeStatusUpdate;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
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

        public UserProfile build() {
            return new UserProfile(userImgLink, name, age, birthDate, address, phoneNumber, bloodType, email, status, latestTimeStatusUpdate);
        }
    }
}