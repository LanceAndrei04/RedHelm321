package com.example.redhelm321;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        InitializeAuth();
        // Inflate the layout for this fragment
        signOut();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void signOut() {
        mAuth.signOut();
    }

    private void InitializeAuth() {
        mAuth = FirebaseAuth.getInstance();
    }



}