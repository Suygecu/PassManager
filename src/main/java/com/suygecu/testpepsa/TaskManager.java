package com.suygecu.testpepsa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaskManager {
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    // Метод для загрузки всех задач из базы данных в ObservableList
    public ObservableList<Task> getObservableTasks() {
        return tasks;
    }

    // Метод для добавления задачи в список и базу данных
    public void addTask(Task task) {
        tasks.add(task);
        saveTaskToDatabase(task);
    }

    // Метод для сохранения задачи в базу данных
    private void saveTaskToDatabase(Task task) {
        String insertSQL = "INSERT INTO tasks (title, description, date) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

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

    // Метод для удаления всех задач из базы данных и списка
    public void clearAllTasks() {
        tasks.clear();
        String clearSQL = "DELETE FROM tasks";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(clearSQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки всех задач из базы данных в ObservableList
    public void loadTasksFromDatabase() {
        String selectSQL = "SELECT id, title, description, date FROM tasks";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            tasks.clear(); // очищаем перед добавлением из базы
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate date = resultSet.getDate("date") != null
                        ? resultSet.getDate("date").toLocalDate() : null;

                Task task = new Task(title, description, date);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для обновления существующей задачи в базе данных
    public void updateTaskInDatabase(Task task) {
        String updateSQL = "UPDATE tasks SET title = ?, description = ?, date = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            if (task.getDate() != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(task.getDate()));
            } else {
                preparedStatement.setNull(3, java.sql.Types.DATE);
            }
            preparedStatement.setInt(4, task.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
