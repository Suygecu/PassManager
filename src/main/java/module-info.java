module com.suygecu.testpepsa {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    opens com.suygecu.testpepsa to javafx.fxml;
    exports com.suygecu.testpepsa;
}