package com.suygecu.testpepsa.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

import static com.suygecu.testpepsa.client.TaskManagerApp.showAlert;

public class SpringJavaFXApplication extends Application {


    private static ApplicationContext context;
    private TaskManagerApp taskMangerApp;

    @Override
    public void init() throws Exception {

        context = new AnnotationConfigApplicationContext(AppConfig.class);
        taskMangerApp = context.getBean(TaskManagerApp.class);
    }

    public static ApplicationContext getContext() {
        return context;
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            ClientHandler.connectToServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            taskMangerApp.loadTasksFromDatabase();  // Загружаем задачи из базы данных
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить задачи при старте приложения.");
        }


        Button button = new Button("Добавить задачу");


        ListView<Task> taskListView = new ListView<>(taskMangerApp.getTaskManager().getObservableTasks());

        taskListView.setCellFactory(param -> new ListCell<>() {
            private final Button editButton = new Button("Редактировать");

            {
                editButton.setOnAction(event -> {
                    Task selectedTask = getItem();
                    if (selectedTask != null) {
                        taskMangerApp.openEditTaskWindow(selectedTask);  // Открываем окно для редактирования задачи
                    }
                });
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(task.toString());
                    setGraphic(editButton);
                }
            }
        });

        button.setOnAction(actionEvent -> {
            taskMangerApp.openAddTaskWindow(taskListView);  // Открываем окно для добавления новой задачи
        });

        // Основной макет приложения
        VBox layout = new VBox(5);
        layout.getChildren().addAll(taskListView, button);

        Scene scene = new Scene(layout, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Мой задачник");
        stage.show();
    }


}
