package com.example.redhelm321;

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

import com.example.redhelm321.adapters.MessageAdapter;
import com.example.redhelm321.models.Message;
import com.example.redhelm321.models.RippleView;


public class ConnectFragment extends Fragment {

    private static final String TAG = "ConnectFragment";

    Button scan_nearby_people, scanButton;
    CardView cardViewAvailableDevices;
    ListView listViewDevices;
    TextView textViewDescription;
    ConstraintLayout chatConstraintLayout, constraintLayoutTitle;
    private RippleView rippleView;

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton btnSend;
    private MessageAdapter messageAdapter;
    private boolean isDetecting = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        InitializeComponents(rootView);

        String[] sampleData = {"Device 1", "Device 2", "Device 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, sampleData);
        listViewDevices.setAdapter(adapter);

        return rootView;
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
                scan_users_onClick();
            }
        });

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cardViewAvailableDevices.setVisibility(View.GONE);
                chatConstraintLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Create and add the message
            Message message = new Message(messageText, true);
            messageAdapter.addMessage(message);

            // Clear input and scroll to bottom
            messageInput.setText("");
            chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void scan_users_onClick() {
        Toast.makeText(getContext(), "Scanning nearby devices...", Toast.LENGTH_SHORT).show();
    }

    private void scan_nearby_people_OnClick() {
        if (isDetecting) {
            rippleView.stopRippleEffect();
            scan_nearby_people.setVisibility(View.GONE);
            textViewDescription.setVisibility(View.GONE);
            constraintLayoutTitle.setVisibility(View.GONE);
            cardViewAvailableDevices.setVisibility(View.VISIBLE);

        } else {
            rippleView.startRippleEffect(scan_nearby_people.getWidth());
        }
        isDetecting = !isDetecting;
    }


}