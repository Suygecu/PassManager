module com.suygecu.testpepsa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.jdbc;

    // Экспортируем и открываем клиентский пакет для JavaFX и Spring
    exports com.suygecu.testpepsa.client to javafx.graphics;
    opens com.suygecu.testpepsa.client to javafx.fxml, spring.beans, spring.context, spring.core;

    // Экспортируем серверный пакет и открываем его для JavaFX и Spring
    exports com.suygecu.testpepsa.server;
    opens com.suygecu.testpepsa.server to javafx.fxml, spring.beans, spring.context, spring.core;

    // Добавляем нужные Spring модули для работы с Spring Boot
    requires spring.web;
    requires spring.data.jpa;
}