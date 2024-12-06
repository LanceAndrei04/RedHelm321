package com.example.redhelm321.connect_nearby;

import android.os.Handler;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.example.redhelm321.MainActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final String serverIP;
    Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final Handler handler;
    private final FragmentActivity sourceActivity;
    private final ReceiveMessageCallback receiveMessageCallback;

    public Client(FragmentActivity sourceActivity, String serverIP, ReceiveMessageCallback receiveMessageCallback) {
        this.serverIP = serverIP;
        this.sourceActivity = sourceActivity;
        this.receiveMessageCallback = receiveMessageCallback;
        this.handler = new Handler();
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(serverIP, MainActivity.IP_PORT);
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            while (true) {
                String sender = inputStream.readUTF();
                String message = inputStream.readUTF();
                String type = inputStream.readUTF();

                ChatMessage chatMessage = new ChatMessage.Builder(sender, message)
                        .setType(type)
                        .build();

                handler.post(() -> {
                    receiveMessageCallback.updateMessageUI(chatMessage);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ChatMessage message) {
        new Thread(() -> {
            try {
                outputStream.writeUTF(message.getSender());
                outputStream.writeUTF(message.getMessage());
                outputStream.writeUTF(message.getType());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(sourceActivity, "Error sending message", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
