package com.suygecu.testpepsa.server;

import com.suygecu.testpepsa.client.DatabaseConnection;
import com.suygecu.testpepsa.client.Task;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerConnect {

    private static BlockingDeque<Task> taskQueue = new LinkedBlockingDeque<>();

    private static boolean isRunningServer = true;


    public static void main(String[] args) throws IOException {

        new Thread(ServerConnect::processQueue).start();

        try (ServerSocket serverSocket = new ServerSocket(1488)) {System.out.println("Сервер запущен чочел тварь ");
            while (isRunningServer){
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клиент подключен");

                    new Thread(() -> handleClient(clientSocket)).start();
                }catch (IOException e){
                    e.printStackTrace();
                }



}
        }

    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream inputClientStream = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream outputStreamClient = new ObjectOutputStream(clientSocket.getOutputStream())) {
while (true){

    Task task = (Task) inputClientStream.readObject();
    System.out.println("Получена задача: " + task);

    taskQueue.put(task);


    outputStreamClient.writeObject("Задача сохранена в базу данных  " + task);
    outputStreamClient.flush();
}
        } catch (ClassNotFoundException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void processQueue() {
        while (true) {
            try {
                Task task = ServerConnect.taskQueue.take();
                System.out.println("Обработка задачи: " + task);
                saveNewTaskToDatabase(task);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }


    private static void saveNewTaskToDatabase(Task task) {
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
            showAlert("Ошибка", "Не удалось сохранить задачу в базу данных.");
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




