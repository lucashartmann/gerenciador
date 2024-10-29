module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    exports main;
    exports model;
    exports service;
    
    opens model to com.google.gson;
}