module com.atmbanksimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.junit.jupiter.api;


    opens com.atmbanksimulator to javafx.fxml;
    exports com.atmbanksimulator;
}