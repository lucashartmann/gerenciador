package main;

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
import javafx.stage.Stage;
import model.FileTag;
import service.FileTagManager;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import model.TaggedFile;

public class MainApp extends Application {
    private FileTagManager fileTagManager;
    private ListView<TaggedFile> fileListView;
    private ObservableList<TaggedFile> fileObservableList;
    private ListView<FileTag> tagListView;
    private ObservableList<FileTag> tagObservableList;
    private ObservableList<TaggedFile> taggedFiles; // Nova declaração

    public MainApp() {
        // Inicializando a lista
        this.taggedFiles = FXCollections.observableArrayList();
        this.fileObservableList = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage primaryStage) {
        fileTagManager = new FileTagManager();
        fileTagManager.loadFromPersistence(); // Carrega arquivos do JSON

        // Primeiro carrega os arquivos do Downloads
        loadFilesFromDownloads();

        // Depois adiciona os arquivos que já têm tags do JSON
        for (TaggedFile taggedFile : fileTagManager.getFiles()) {
            if (!fileObservableList.contains(taggedFile)) {
                fileObservableList.add(taggedFile);
            }
        }

        fileListView = new ListView<>(fileObservableList); // Cria a ListView para arquivos

        // Customiza a exibição dos arquivos na ListView
        fileListView.setCellFactory(lv -> new ListCell<TaggedFile>() {
            @Override
            protected void updateItem(TaggedFile file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(file.toString());
                    if (file.isDirectory()) {
                        setText("📁 " + file.toString());
                    }
                    if (!file.getTags().isEmpty()) {
                        FileTag firstTag = file.getTags().iterator().next();
                        setStyle("-fx-text-fill: " + convertColorToHex(firstTag.getColor()) + ";");
                    } else {
                        setStyle("-fx-text-fill: #FFFFFF;");
                    }
                }
            }
        });

        // Adiciona double-click handler
        fileListView.setOnMouseClicked(event -> {
            TaggedFile selectedItem = fileListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && event.getClickCount() == 2 && selectedItem.isDirectory()) {
                loadFilesFromDirectory(selectedItem.getFilePath());
            }
        });

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // Adicione os estilos CSS para tema escuro
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Painel esquerdo: Lista de arquivos
        VBox leftPanel = new VBox(10, fileListView);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(400);

        // Painel direito: Gerenciador de tags
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));

        // Campo de pesquisa
        TextField searchField = new TextField();
        searchField.setPromptText("Pesquisar por tag...");

        // Lista de arquivos com tags
        Label taggedFilesLabel = new Label("Arquivos com Tags:");
        ListView<TaggedFile> taggedFilesListView = new ListView<>();

        // Handler para pesquisa
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                taggedFilesListView.setItems(FXCollections.observableArrayList(
                        fileTagManager.getFiles()));
            } else {
                // Filtra os arquivos que contêm a tag pesquisada
                List<TaggedFile> filteredFiles = fileTagManager.searchByTag(newValue.trim());
                taggedFilesListView.setItems(FXCollections.observableArrayList(filteredFiles));
            }
        });

        // Atualiza a lista de arquivos com tags
        ObservableList<TaggedFile> taggedFiles = FXCollections.observableArrayList(
                fileTagManager.getFiles().stream()
                        .filter(file -> !file.getTags().isEmpty())
                        .collect(Collectors.toList()));
        taggedFilesListView.setItems(taggedFiles);

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
                System.out.println("Adicionando tag ao arquivo: " + selectedFile.getFilePath());

                // Cria e adiciona a tag
                FileTag tag = new FileTag(tagName, tagColor);
                fileTagManager.addTagToFile(selectedFile.getFilePath(), tag);

                // Atualiza as visualizações
                updateTagList(selectedFile);

                // Atualiza a lista de arquivos com tags
                taggedFilesListView.setItems(FXCollections.observableArrayList(
                        fileTagManager.getFiles().stream()
                                .filter(file -> !file.getTags().isEmpty())
                                .collect(Collectors.toList())));

                // Limpa o campo de texto
                tagNameField.clear();

                // Salva as alterações
                fileTagManager.saveToPersistence();

                // Atualiza a visualização
                fileListView.refresh();

                System.out.println("Tag adicionada com sucesso!");
            }
        });

        HBox tagInputBox = new HBox(10, tagNameField, tagColorPicker, addTagButton);

        // Lista de tags
        tagObservableList = FXCollections.observableArrayList();
        tagListView = new ListView<>(tagObservableList);

        // Lista de tags (modificada para mostrar cores)
        tagListView.setCellFactory(lv -> new ListCell<FileTag>() {
            @Override
            protected void updateItem(FileTag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty || tag == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(tag.getName());
                    setStyle("-fx-text-fill: " + convertColorToHex(tag.getColor()) + ";");
                }
            }
        });

        // Botão para editar cor da tag
        Button editColorButton = new Button("Editar Cor");
        editColorButton.setOnAction(e -> {
            FileTag selectedTag = tagListView.getSelectionModel().getSelectedItem();
            if (selectedTag != null) {
                Color newColor = tagColorPicker.getValue();
                selectedTag.setColor(newColor);
                TaggedFile selectedFile = fileListView.getSelectionModel().getSelectedItem();
                updateTagList(selectedFile);
                fileTagManager.saveToPersistence();
                fileListView.refresh();
            }
        });

        // Botão para abrir arquivo
        Button openFileButton = new Button("Abrir Arquivo");
        openFileButton.setOnAction(e -> {
            TaggedFile selectedFile = fileListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null && !selectedFile.isDirectory()) {
                try {
                    java.awt.Desktop.getDesktop().open(selectedFile.getFilePath().toFile());
                } catch (Exception ex) {
                    showAlert("Erro ao abrir arquivo", ex.getMessage());
                }
            }
        });

        // Adiciona os novos botões ao painel
        HBox buttonBox = new HBox(10, openFileButton, editColorButton);
        rightPanel.getChildren().add(buttonBox);

        // Adiciona componentes ao painel direito (ordem atualizada)
        rightPanel.getChildren().addAll(
                new Label("Pesquisar arquivos por tag:"),
                searchField,
                new Separator(),
                taggedFilesLabel,
                taggedFilesListView,
                new Separator(),
                new Label("Tags do arquivo selecionado:"),
                tagInputBox,
                tagListView);

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
                    TaggedFile taggedFile = new TaggedFile(filePath);
                    // Verifica se o arquivo já está na lista
                    if (!fileObservableList.contains(taggedFile)) {
                        System.out.println("File added: " + filePath);
                        fileObservableList.add(taggedFile);
                        // Não adiciona ao FileTagManager aqui, apenas quando uma tag for adicionada
                    }
                }
            }
        }
    }

    private void updateTagList(TaggedFile taggedFile) {
        tagObservableList.setAll(taggedFile.getTags());
        // Atualiza a visualização do arquivo selecionado
        fileListView.refresh();
    }

    // Método auxiliar para converter Color para código hexadecimal CSS
    private String convertColorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void loadFilesFromDirectory(Path directory) {
        fileObservableList.clear();
        File dir = directory.toFile();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                TaggedFile taggedFile = new TaggedFile(file.toPath());
                fileObservableList.add(taggedFile);
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
