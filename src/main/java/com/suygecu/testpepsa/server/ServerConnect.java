package com.suygecu.testpepsa.server;

import com.suygecu.testpepsa.client.DatabaseConnection;
import com.suygecu.testpepsa.client.TaskPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerConnect {



    private static boolean isRunningServer = true;

    public static void main(String[] args) throws SQLException, IOException {
        serverConnect();
    }




    public static void serverConnect ()throws IOException, SQLException {
            DatabaseConnection.getInstance().getConnection();
            try (ServerSocket serverSocket = new ServerSocket(1488)) {
                System.out.println("Сервер запущен и ожидает подключения клиентов...");
                while (isRunningServer) {
                    try {

                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Клиент подключен");


                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }


    private static void handleClient(Socket clientSocket) {
        try (DataInputStream inputClientStream = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream outputStreamClient = new DataOutputStream(clientSocket.getOutputStream())) {

            while (true) {

                TaskPacket packet = new TaskPacket();
                packet.readPacket(inputClientStream);

                System.out.println("Получена задача: " + packet);
                packet.processPacket();


                packet.writePacket(outputStreamClient);
                outputStreamClient.flush();
                System.out.println("Ответ от клиента, задача получена: " + packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
