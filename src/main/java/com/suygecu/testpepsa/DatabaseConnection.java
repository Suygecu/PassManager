package com.suygecu.testpepsa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";  // Заменить на твое имя пользователя MySQL
    private static final String PASSWORD = "vell123009";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Успешное подключение к базе данных");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка подключения к базе данных.");
        }
        return connection;
    }
}
