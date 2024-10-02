package com.suygecu.testpepsa.client;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;


    public ClientHandler(Socket clientSocket) throws Exception {
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
        this.clientSocket = clientSocket;
    }

    public void sendTask(Task task) throws IOException {
        try {

            outputStream.writeUTF(task.getTitle());
            outputStream.writeUTF(task.getDescription());

            if(task.getDate() != null){
                outputStream.writeUTF(task.getDate().toString());
            }else {
                outputStream.writeUTF("");
            }
            outputStream.flush();
            System.out.println("Ответ от сервера: " + task);

            String serverResponse = inputStream.readUTF();
            System.out.println("Ответ от сервера: " + serverResponse);


        } catch (IOException e){
            e.printStackTrace();
        }
        }
    }








