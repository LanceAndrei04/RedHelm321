package com.example.redhelm321.connect_nearby;

import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    ServerSocket serverSocket;

    FragmentActivity sourceActivity;
    ExecutorService threadPool;
    ArrayList<ConnectionHandler> connections;



    Handler handler;
    private boolean isRunning;

    public Server(FragmentActivity sourceActivity) {
        this.connections = new ArrayList<>();
        this.sourceActivity = sourceActivity;
        this.handler = new Handler();
        this.isRunning = true;
    }

    public void broadcastMessage(String message) {
//        handler.post(() -> updateMessageUI(message));
        for (ConnectionHandler connection : connections) {
            connection.sendMessage(message);
        }
    }

//    private void updateMessageUI(String message) {
//        messages.add(message);
//        lvMessageList.setAdapter(new ArrayAdapter<String>(
//                sourceActivity,
//                android.R.layout.simple_list_item_1,
//                messages));
//    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(MainActivity.IP_PORT);
            threadPool = Executors.newCachedThreadPool();
            //handle the socket that was accepted, create a connection handler for it
            while(isRunning) {
                Socket newClientSocket = serverSocket.accept() ;
                if (newClientSocket!= null) {
                    ConnectionHandler connectionHandler = new ConnectionHandler(newClientSocket);
                    connections.add(connectionHandler);
                    threadPool.execute(connectionHandler);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // CONNECTION HANDLER CLASS //
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // -----------------------------------------------------------------//
    // CONNECTION HANDLER CLASS //

    class ConnectionHandler extends Thread {

        Socket clientSocket;
        DataInputStream inputStream;
        DataOutputStream outputStream;

        public ConnectionHandler(Socket clientSocket) {

            handler.post(() -> Toast.makeText(sourceActivity, clientSocket.getInetAddress() + " connected", Toast.LENGTH_SHORT).show());

            this.clientSocket = clientSocket;
            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {

            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());

                outputStream.writeUTF("SUCCESSFULLY CONNECTED!");

                String inMessage;
                while ((inMessage = inputStream.readUTF()) != null) {
                    String finalInMessage = inMessage;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            receiveMessage(finalInMessage);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void receiveMessage(String finalInMessage) {
            //TODO: Receive message from server
            broadcastMessage(finalInMessage);
        }

        public void sendMessage(String message) {
            new Thread(() -> {
                try {
                    if (outputStream != null) {
                        outputStream.writeUTF(message);
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> Toast.makeText(sourceActivity, "Error sending message", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }


    }
}