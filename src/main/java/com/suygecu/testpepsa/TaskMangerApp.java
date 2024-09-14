package com.suygecu.testpepsa;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaskMangerApp extends Application {

    // Экземпляр класса TaskManager для управления задачами
    private TaskManager taskManager = new TaskManager();

    // Компонент ListView для отображения списка задач
    private ListView<Task> taskListView;

    // Метод для отображения всплывающего окна с сообщениями
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Показываем всплывающее окно
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Загружаем задачи из базы данных при запуске приложения
        loadTasksFromDatabase();

        // Кнопка для добавления новой задачи
        Button button = new Button("Добавить задачу");

        // Создаем ListView для отображения задач
        taskListView = new ListView<>(taskManager.getObservableTasks());

        // Настраиваем фабрику ячеек для ListView, чтобы добавить кнопку "Редактировать"
        taskListView.setCellFactory(param -> new ListCell<>() {
            private final Button editButton = new Button("Редактировать");

            {
                // Логика кнопки "Редактировать" — открытие окна редактирования задачи
                editButton.setOnAction(event -> {
                    Task selectedTask = getItem();  // Получаем задачу для текущей строки
                    if (selectedTask != null) {
                        openEditTaskWindow(selectedTask);  // Открываем окно для редактирования задачи
                    }
                });
            }

            // Обновляем содержимое ячейки (строки) в ListView
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);  // Если задачи нет, очищаем ячейку
                } else {
                    setText(task.toString());  // Отображаем текст задачи
                    setGraphic(editButton);  // Добавляем кнопку "Редактировать"
                }
            }
        });

        // Логика кнопки "Добавить задачу" — открытие окна для добавления новой задачи
        button.setOnAction((actionEvent -> openAddTaskWindow()));

        // Создаём макет VBox для вертикального размещения элементов
        VBox layout = new VBox(5);
        layout.getChildren().addAll(taskListView, button);  // Добавляем список задач и кнопку в макет

        // Создаём и настраиваем сцену
        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);  // Устанавливаем сцену на окно
        primaryStage.setTitle("Мой задачник");
        primaryStage.show();  // Показываем окно

        // Сохраняем задачи в базу данных при закрытии приложения
        primaryStage.setOnCloseRequest(windowEvent -> saveAllTasksToDatabase());
    }

    // Метод для сохранения всех задач в базу данных
    private void saveAllTasksToDatabase() {
        String insertTaskSQL = "INSERT INTO tasks (title, description, date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL)) {

            for (Task task : taskManager.getObservableTasks()) {
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription());
                if (task.getDate() != null) {
                    preparedStatement.setDate(3, java.sql.Date.valueOf(task.getDate()));
                } else {
                    preparedStatement.setNull(3, java.sql.Types.DATE);
                }

                preparedStatement.executeUpdate();  // Сохраняем задачу в базу данных
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось сохранить задачи в базу данных.");
        }
    }

    // Метод для загрузки задач из базы данных
    private void loadTasksFromDatabase() {
        String selectTasksSQL = "SELECT title, description, date FROM tasks";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectTasksSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate date = resultSet.getDate("date") != null
                        ? resultSet.getDate("date").toLocalDate() : null;

                Task task = new Task(title, description, date);
                taskManager.addTask(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить задачи из базы данных.");
        }
    }

    // Открытие окна для редактирования задачи
    private void openEditTaskWindow(Task task) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редактировать задачу");
        window.setResizable(true);  // Разрешаем изменение размеров окна

        // Создаём поля для редактирования задачи
        TextField taskNameField = new TextField(task.getTitle());
        TextArea taskDescriptionField = new TextArea(task.getDescription());
        DatePicker datePicker = new DatePicker(task.getDate());

        Button submitButton = new Button("Сохранить изменения");

        // Макет для размещения полей
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Размещаем поля и метки в макете
        gridPane.add(new Label("Название задачи"), 0, 0);
        gridPane.add(taskNameField, 1, 0);
        gridPane.add(new Label("Описание задачи"), 0, 1);
        gridPane.add(taskDescriptionField, 1, 1);
        gridPane.add(new Label("Дата задачи"), 0, 2);
        gridPane.add(datePicker, 1, 2);

        // Логика кнопки "Сохранить изменения"
        submitButton.setOnAction(e -> {
            String newTitle = taskNameField.getText();
            String newDescription = taskDescriptionField.getText();
            LocalDate newDate = datePicker.getValue();

            // Проверяем, что все поля заполнены
            if (newTitle.isEmpty() || newDescription.isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля");
            } else {
                // Обновляем задачу новыми данными
                task.setTitle(newTitle);
                task.setDescription(newDescription);
                task.setDate(newDate);

                // Обновляем отображение списка задач
                taskListView.setItems(taskManager.getObservableTasks());
                showAlert("Задача обновлена", "Задача: " + newTitle + "\nОписание: " + newDescription);
                window.close();  // Закрываем окно после сохранения
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(gridPane, submitButton);

        Scene dialogScene = new Scene(dialogLayout, 600, 450);
        window.setScene(dialogScene);
        window.showAndWait();  // Показываем окно и ждём его закрытия
    }

    // Открытие окна для добавления новой задачи
    private void openAddTaskWindow() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Добавить новую задачу");
        window.setResizable(true);  // Разрешаем изменение размеров окна

        // Поля для добавления новой задачи
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Название задачи");

        TextArea taskDescriptionField = new TextArea();
        taskDescriptionField.setPromptText("Описание задачи");
        taskDescriptionField.setPrefHeight(100);
        taskDescriptionField.setWrapText(true);

        DatePicker datePicker = new DatePicker();

        Button submitButton = new Button("Добавить");

        // Макет для полей
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Размещаем поля и метки в макете
        GridPane.setHgrow(taskNameField, Priority.ALWAYS);
        GridPane.setHgrow(taskDescriptionField, Priority.ALWAYS);
        GridPane.setVgrow(taskDescriptionField, Priority.ALWAYS);

        gridPane.add(new Label("Название задачи"), 0, 0);
        gridPane.add(taskNameField, 1, 0);
        gridPane.add(new Label("Описание задачи"), 0, 1);
        gridPane.add(taskDescriptionField, 1, 1);
        gridPane.add(new Label("Дата задачи"), 0, 2);
        gridPane.add(datePicker, 1, 2);

        // Логика кнопки "Добавить" — сохранение новой задачи
        submitButton.setOnAction(e -> {
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            LocalDate taskDate = datePicker.getValue();

            // Проверяем, что все поля заполнены
            if (taskName.isEmpty() || taskDescription.isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля");
            } else {
                // Создаём новую задачу и добавляем её в TaskManager
                Task newTask = new Task(taskName, taskDescription, taskDate);
                taskManager.addTask(newTask);  // Добавляем задачу в TaskManager
                taskListView.setItems(taskManager.getObservableTasks());  // Обновляем список задач

                // Сохраняем новую задачу в базу данных
                saveNewTaskToDatabase(newTask);

                // Показываем сообщение об успешном добавлении задачи
                showAlert("Задача добавлена", "Задача: " + taskName + "\nОписание: " + taskDescription);
                window.close();  // Закрываем окно после добавления задачи
            }
        });

        // Создаём макет VBox для размещения полей и кнопки
        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(gridPane, submitButton);

        // Создаём сцену для окна добавления задачи
        Scene dialogScene = new Scene(dialogLayout, 600, 450);
        window.setScene(dialogScene);
        window.showAndWait();  // Показываем окно и ждём его закрытия
    }

    // Метод для сохранения новой задачи в базу данных
    private void saveNewTaskToDatabase(Task task) {
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

            preparedStatement.executeUpdate();  // Выполняем вставку задачи в базу данных

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось сохранить задачу в базу данных.");
        }
    }
}
