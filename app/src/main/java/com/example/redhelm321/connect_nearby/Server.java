package com.example.redhelm321.connect_nearby;

import android.os.Handler;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.example.redhelm321.MainActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private final FragmentActivity sourceActivity;
    private final ArrayList<ConnectionHandler> connections = new ArrayList<>();
    private final ReceiveMessageCallback receiveMessageCallback;
    private final Handler handler;
    private boolean isRunning = true;
    private ExecutorService threadPool;

    public Server(FragmentActivity sourceActivity, ReceiveMessageCallback receiveMessageCallback) {
        this.sourceActivity = sourceActivity;
        this.receiveMessageCallback = receiveMessageCallback;
        this.handler = new Handler();
    }

    public void broadcastMessage(ChatMessage message) {
        for (ConnectionHandler connection : connections) {
            connection.sendMessage(message);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(MainActivity.IP_PORT);
            threadPool = Executors.newCachedThreadPool();
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
                connections.add(connectionHandler);
                threadPool.execute(connectionHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ConnectionHandler extends Thread {
        private final Socket clientSocket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public ConnectionHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String sender = inputStream.readUTF();
                    String message = inputStream.readUTF();
                    String type = inputStream.readUTF();

                    ChatMessage chatMessage = new ChatMessage.Builder(sender, message)
                            .setType(type)
                            .build();

                    handler.post(() -> {
                        Toast.makeText(sourceActivity, "TYPE: " + type , Toast.LENGTH_SHORT).show();
                        receiveMessageCallback.updateMessageUI(chatMessage);
//                        broadcastMessage(chatMessage);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(ChatMessage message) {
            try {
                outputStream.writeUTF(message.getSender());
                outputStream.writeUTF(message.getMessage());
                outputStream.writeUTF(message.getType());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
