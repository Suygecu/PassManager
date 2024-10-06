package com.suygecu.testpepsa.client;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public static ClientHandler clientHandler;






    public ClientHandler(Socket clientSocket) throws Exception {
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
        this.clientSocket = clientSocket;
    }



    public static  ClientHandler connectToServer() throws Exception {
        try {
            Socket socket = new Socket("127.0.0.1", 1488);
            clientHandler = new ClientHandler(socket);

            System.out.println("Соединение с сервером установлено.");

        } catch (IOException e) {
            e.printStackTrace();
            TaskMangerApp.showAlert("Ошибка", "Не удалось подключится к серверу");
        }
        return clientHandler;
    }

    public void sendTask(Task task) throws IOException {
        try {

            outputStream.writeUTF(task.getTitle());
            outputStream.writeUTF(task.getDescription());

            if (task.getDate() != null) {
                outputStream.writeUTF(task.getDate().toString());
            } else {
                outputStream.writeUTF("");
            }
            outputStream.flush();
            System.out.println("Ответ от сервера: " + "\n" + "Задача получена: " + task);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }







