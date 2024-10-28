module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson; // Adicione isso se você estiver usando Gson

    opens com.example to javafx.fxml; // Permite que o JavaFX acesse as classes em com.example
    exports com.example; // Exporta o pacote para que possa ser acessado por outros módulos
}
