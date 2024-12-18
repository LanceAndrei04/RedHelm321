package com.example.redhelm321.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.redhelm321.R;
import com.example.redhelm321.models.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_NOTIFICATION = 3;
    private List<Message> messages = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_NOTIFICATION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_notification, parent, false);
            return new NotificationMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageHolder) holder).bind(message);
        } else if (holder.getItemViewType() == VIEW_TYPE_NOTIFICATION) {
            ((NotificationMessageHolder) holder).bind(message);
        } else {
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isNotification()) {
            return VIEW_TYPE_NOTIFICATION;
        }
        return message.isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, senderName;

        SentMessageHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }

        void bind(Message message) {
            senderName.setText(message.getSender() + " • ");
            messageText.setText(message.getText());
            timeText.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }

    class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, senderName;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }

        void bind(Message message) {
            senderName.setText(" • " + message.getSender());
            messageText.setText(message.getText());
            timeText.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }


    class NotificationMessageHolder extends RecyclerView.ViewHolder {
        TextView notificationText;

        NotificationMessageHolder(View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notificationText);
        }

        void bind(Message message) {
            notificationText.setText(message.getText());
        }
    }
}
