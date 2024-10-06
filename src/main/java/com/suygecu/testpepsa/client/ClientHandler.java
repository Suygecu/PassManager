package com.suygecu.testpepsa.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {


    public static ClientHandler clientHandler;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    private final Socket clientSocket;

    public ClientHandler(Socket socket) throws Exception {
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.clientSocket = socket;
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
            String response = inputStream.readUTF();
            System.out.println("Ответ от сервера: " + response + "\n" + "Задача получена: " + task);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static  ClientHandler connectToServer() throws Exception {
        try {
            Socket socket = new Socket("127.0.0.1", 1488);
            clientHandler = new ClientHandler(socket);

            System.out.println("Соединение с сервером установлено.");

        } catch (IOException e) {
            e.printStackTrace();
            TaskManagerApp.showAlert("Ошибка", "Не удалось подключится к серверу");
        }
        return clientHandler;
    }



    }







