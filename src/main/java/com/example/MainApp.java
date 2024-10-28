package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainApp extends Application {
    private FileTagManager fileTagManager;
    private ListView<TaggedFile> fileListView;
    private ObservableList<TaggedFile> fileObservableList;
    private ListView<FileTag> tagListView;
    private ObservableList<FileTag> tagObservableList;
    
    @Override
    public void start(Stage primaryStage) {
        fileTagManager = new FileTagManager();
        fileTagManager.loadFromPersistence();  // Carrega dados persistidos

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        fileObservableList = FXCollections.observableArrayList(fileTagManager.getFiles());
        fileListView = new ListView<>(fileObservableList);
        fileListView.setPrefWidth(400);

        // Painel esquerdo: Lista de arquivos
        VBox leftPanel = new VBox(10, fileListView);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(400);
        
        // Painel direito: Gerenciador de tags
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        
        // Campo de texto para tag
        TextField tagNameField = new TextField();
        tagNameField.setPromptText("Enter tag name");
        
        // Picker de cor para tag
        ColorPicker tagColorPicker = new ColorPicker();
        
        // Botão para adicionar tag
        Button addTagButton = new Button("Add Tag");
        addTagButton.setOnAction(e -> {
            String tagName = tagNameField.getText();
            Color tagColor = tagColorPicker.getValue();
            TaggedFile selectedFile = fileListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null && !tagName.isEmpty()) {
                FileTag tag = new FileTag(tagName, tagColor);
                fileTagManager.addTagToFile(selectedFile.getFilePath(), tag);
                updateTagList(selectedFile);
                fileTagManager.saveToPersistence();  // Salva dados após adicionar tag
            }
        });
        
        HBox tagInputBox = new HBox(10, tagNameField, tagColorPicker, addTagButton);
        
        // Lista de tags
        tagObservableList = FXCollections.observableArrayList();
        tagListView = new ListView<>(tagObservableList);
        
        rightPanel.getChildren().addAll(tagInputBox, tagListView);
        
        // Adiciona painéis ao root
        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        // Atualiza lista de tags ao selecionar um arquivo
        fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateTagList(newSelection);
            }
        });

        primaryStage.setTitle("File Tag Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void loadFilesFromDownloads() {
        File downloadsDir = new File(System.getProperty("user.home"), "Downloads");
        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            for (File file : downloadsDir.listFiles()) {
                if (file.isFile()) {
                    Path filePath = file.toPath();
                    fileTagManager.addFile(filePath);
                    fileObservableList.add(new TaggedFile(filePath));
                }
            }
        }
    }

    private void updateTagList(TaggedFile taggedFile) {
        tagObservableList.setAll(taggedFile.getTags());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
