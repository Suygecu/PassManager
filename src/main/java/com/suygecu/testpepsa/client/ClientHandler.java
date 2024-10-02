package com.suygecu.testpepsa.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    public ClientHandler(Socket clientSocket) throws Exception{
        this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        this.clientSocket = clientSocket;
    }

    public void sendTask(Task task) throws IOException {
        try {

            outputStream.writeObject(task);
            outputStream.flush();


            String serverResponse = (String) inputStream.readObject();
            System.out.println("Ответ от сервера: " + serverResponse);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        }

    }






