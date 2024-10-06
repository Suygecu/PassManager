package com.suygecu.testpepsa.client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

@Component
public class TaskManagerApp {

    private static final String SELECT_TASKS_SQL = "SELECT title, description, date FROM tasks";
    private static final String INSERT_TASK_SQL = "INSERT INTO tasks (title, description, date) VALUES (?, ?, ?)";

    private TaskManager taskManager = new TaskManager();


    private final DataSource dataSource;
    @Autowired
    public TaskManagerApp(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void loadTasksFromDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            loadTasks(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить задачи из базы данных.");
        }
    }

    private void loadTasks(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASKS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate date = resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : null;
                Task task = new Task(title, description, date);
                if (!taskManager.getObservableTasks().contains(task)) {
                    taskManager.addTask(task);
                }
            }
        }
    }

    public void saveNewTaskToDatabase(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK_SQL)) {
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription());
                if (task.getDate() != null) {
                    preparedStatement.setDate(3, Date.valueOf(task.getDate()));
                } else {
                    preparedStatement.setNull(3, Types.DATE);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось сохранить задачу в базе данных.");
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void openAddTaskWindow(ListView<Task> taskListView) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Добавить новую задачу");
        window.setResizable(true);

        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Название задачи");

        TextArea taskDescriptionField = new TextArea();
        taskDescriptionField.setPromptText("Описание задачи");
        taskDescriptionField.setPrefHeight(100);
        taskDescriptionField.setWrapText(true);

        DatePicker datePicker = new DatePicker();

        Button submitButton = new Button("Добавить");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        GridPane.setHgrow(taskNameField, Priority.ALWAYS);
        GridPane.setHgrow(taskDescriptionField, Priority.ALWAYS);
        GridPane.setVgrow(taskDescriptionField, Priority.ALWAYS);

        gridPane.add(new Label("Название задачи"), 0, 0);
        gridPane.add(taskNameField, 1, 0);
        gridPane.add(new Label("Описание задачи"), 0, 1);
        gridPane.add(taskDescriptionField, 1, 1);
        gridPane.add(new Label("Дата задачи"), 0, 2);
        gridPane.add(datePicker, 1, 2);

        submitButton.setOnAction(e -> {
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            LocalDate taskDate = datePicker.getValue();

            if (taskName.isEmpty() || taskDescription.isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля");
            } else {
                Task newTask = new Task(taskName, taskDescription, taskDate);

                new Thread(() -> {
                    try {
                        ClientHandler.clientHandler.sendTask(newTask);

                        Platform.runLater(() -> {
                            taskManager.addTask(newTask);
                            taskListView.getItems().add(newTask);
                            window.close();

                            showAlert("Задача добавлена", "Задача: " + taskName + "\nОписание: " + taskDescription);
                        });

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> showAlert("Ошибка", "Не удалось отправить задачу на сервер"));
                    }
                }).start();
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(gridPane, submitButton);

        Scene dialogScene = new Scene(dialogLayout, 600, 450);
        window.setScene(dialogScene);
        window.showAndWait();
    }

    public void openEditTaskWindow(Task task) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редактировать задачу");
        window.setResizable(true);

        TextField taskNameField = new TextField(task.getTitle());
        TextArea taskDescriptionField = new TextArea(task.getDescription());
        DatePicker datePicker = new DatePicker(task.getDate());

        Button submitButton = new Button("Сохранить изменения");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(new Label("Название задачи"), 0, 0);
        gridPane.add(taskNameField, 1, 0);
        gridPane.add(new Label("Описание задачи"), 0, 1);
        gridPane.add(taskDescriptionField, 1, 1);
        gridPane.add(new Label("Дата задачи"), 0, 2);
        gridPane.add(datePicker, 1, 2);

        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(gridPane, submitButton);

        Scene dialogScene = new Scene(dialogLayout, 600, 450);
        window.setScene(dialogScene);
        window.showAndWait();
    }
}