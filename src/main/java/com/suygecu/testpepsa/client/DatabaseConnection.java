package com.suygecu.testpepsa.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;

    private  Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";
    private static final String PASSWORD = "vell123009";

    private DatabaseConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                System.out.println("Успешное подключение к базе данных (Singleton).");
            }
            else {
                System.out.println("Используется существующее подключение (Singleton).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка подключения к базе данных (Singleton).");
            throw new SQLException("Ошибка подключения к базе данных.", e);  // Бросаем исключение
        }
    }


    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (instance != null && instance.connection != null && !instance.connection.isClosed()) {
            instance.connection.close();
            System.out.println("Соединение закрыто.");
        }
    }

}
