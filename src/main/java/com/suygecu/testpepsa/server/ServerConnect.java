package com.suygecu.testpepsa.server;

import com.suygecu.testpepsa.client.DatabaseConnection;
import com.suygecu.testpepsa.client.Task;
import com.suygecu.testpepsa.client.TaskPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerConnect {

    public static BlockingDeque<Task> taskQueue = new LinkedBlockingDeque<>();

    private static boolean isRunningServer = true;


    public static void main(String[] args) throws IOException {

        new Thread(ServerConnect::processQueue).start();

        try (ServerSocket serverSocket = new ServerSocket(1488)) {
            System.out.println("Сервер запущен чочел тварь ");
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

                System.out.println("Отправляем подтверждение клиенту");

                packet.writePacket(outputStreamClient);
                outputStreamClient.flush();
                System.out.println("Подтверждение отправлено клиенту");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processQueue() {
        while (true) {
            try {

                Task task = taskQueue.take();
                System.out.println("Обрабатываем задачу: " + task);


                saveNewTaskToDatabase(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void saveNewTaskToDatabase(Task task) {
        System.out.println("Сохраняем новую задачу в базу данных: " + task);
        String insertTaskSQL = "INSERT INTO tasks (title, description, date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL)) {

            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            if (task.getDate() != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(task.getDate()));
            } else {
                preparedStatement.setNull(3, java.sql.Types.DATE);
            }

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}




