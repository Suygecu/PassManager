module com.suygecu.testpepsa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.suygecu.testpepsa.client;
    exports com.suygecu.testpepsa.server;


    opens com.suygecu.testpepsa.client to javafx.fxml;
    opens com.suygecu.testpepsa.server to javafx.fxml;
}