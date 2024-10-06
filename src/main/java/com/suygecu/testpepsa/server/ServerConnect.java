package com.suygecu.testpepsa.server;

import com.suygecu.testpepsa.client.TaskPacket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootApplication(proxyBeanMethods = false)
public class ServerConnect {

    private static boolean isRunningServer = true;

    public static void main(String[] args) {
        SpringApplication.run(ServerConnect.class, args);

        try {
            startSocketServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startSocketServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(1488)) {
            System.out.println("Сокет-сервер запущен и ожидает подключения клиентов...");

            while (isRunningServer) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен");

                // Обрабатываем каждого клиента в отдельном потоке
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (DataInputStream inputClientStream = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream outputStreamClient = new DataOutputStream(clientSocket.getOutputStream())) {

            TaskPacket packet = new TaskPacket();
            packet.readPacket(inputClientStream);

            System.out.println("Получена задача: " + packet);
            String result = packet.processPacket();

            // Отправляем подтверждение клиенту
            outputStreamClient.writeUTF(result);
            outputStreamClient.flush();
            System.out.println("Ответ клиенту отправлен: " + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
