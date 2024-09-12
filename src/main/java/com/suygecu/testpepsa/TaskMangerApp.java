package com.suygecu.testpepsa;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TaskMangerApp extends Application {

    boolean isRed = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Circle circle = new Circle(50);
        circle.setFill(Color.RED);
        Text text = new Text("Тут текст");
        text.setFont(Font.font(22));
        text.setFill(Color.BLUE);
        StackPane pane = new StackPane();
        pane.getChildren().addAll(circle, text);

        Button button = new Button();
        pane.getChildren().add(button);
        VBox layout = new VBox(1);
        layout.getChildren().addAll(pane, button);
        button.setOnAction(actionEvent -> {
            if (isRed) {
                circle.setFill(Color.PURPLE);
            } else {
                circle.setFill(Color.RED);
            }
            isRed = !isRed;
        });


        Scene scene = new Scene(layout, 500, 500);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Task Manager");
        primaryStage.show();
    }
}
