package com.example.redhelm321;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.redhelm321.adapters.MessageAdapter;
import com.example.redhelm321.models.Message;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton btnSend;
    private MessageAdapter messageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        btnSend = view.findViewById(R.id.btnSend);

        // Setup RecyclerView
        messageAdapter = new MessageAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(messageAdapter);

        // Setup send button click listener
        btnSend.setOnClickListener(v -> sendMessage());
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
}