package com.example.redhelm321;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotlineFragment extends Fragment implements HotlineAdapter.OnHotlineClickListener {
    private RecyclerView hotlineRecyclerView;
    private HotlineAdapter hotlineAdapter;
    private FloatingActionButton addHotlineFab;
    private List<HotlineItem> hotlineList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotline, container, false);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view) {
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("hotlines");

        // Initialize RecyclerView
        hotlineRecyclerView = view.findViewById(R.id.hotlineRecyclerView);
        hotlineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hotlineList = new ArrayList<>();
        hotlineAdapter = new HotlineAdapter(hotlineList, this);
        hotlineRecyclerView.setAdapter(hotlineAdapter);

        // Initialize FAB
        addHotlineFab = view.findViewById(R.id.addHotlineFab);
        addHotlineFab.setOnClickListener(v -> showAddHotlineDialog());

        // Load hotlines from Firebase
        loadHotlines();
    }

    private void loadHotlines() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotlineList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HotlineItem hotline = dataSnapshot.getValue(HotlineItem.class);
                    if (hotline != null) {
                        hotlineList.add(hotline);
                    }
                }
                hotlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEBUG_HOTLINE", "Failed to load hotlines: " + error.getMessage());
            }
        });
    }

    private void showAddHotlineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Hotline");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_hotline, null);
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText numberInput = dialogView.findViewById(R.id.numberInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String number = numberInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (!name.isEmpty() && !number.isEmpty()) {
                String id = mDatabase.push().getKey();
                HotlineItem newHotline = new HotlineItem(id, name, number, description);
                if (id != null) {
                    mDatabase.child(id).setValue(newHotline)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                                    "Hotline added successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(),
                                    "Failed to add hotline", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(getContext(), "Name and number are required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    @Override
    public void onHotlineClick(HotlineItem hotline) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + hotline.getNumber()));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(HotlineItem hotline) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Hotline")
                .setMessage("Are you sure you want to delete this hotline?")
                .setPositiveButton("Delete", (dialog, which) -> onDeleteConfirmed(hotline))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDeleteConfirmed(HotlineItem hotline) {
        if (hotline.getId() != null) {
            mDatabase.child(hotline.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                            "Hotline deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Failed to delete hotline", Toast.LENGTH_SHORT).show());
        }
    }
}