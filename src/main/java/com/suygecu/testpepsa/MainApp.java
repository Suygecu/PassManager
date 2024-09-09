package com.suygecu.testpepsa;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        InterfaceGui interfaceGui = new InterfaceGui();
        interfaceGui.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}