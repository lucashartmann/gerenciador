package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.FileTag;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.TaggedFile;

public class FileTagManager {
    private List<TaggedFile> files;
    private static final String PERSISTENCE_FILE = "src/main/resources/file_tags.json";

    public FileTagManager() {
        this.files = new ArrayList<>();
    }

    public void addFile(Path filePath) {
        boolean fileExists = files.stream()
                .anyMatch(file -> file.getFilePath().equals(filePath));
        
        if (!fileExists) {
            files.add(new TaggedFile(filePath));
        }
    }

    public void addTagToFile(Path filePath, FileTag tag) {
        System.out.println("Adicionando tag '" + tag.getName() + "' ao arquivo: " + filePath);
        
        TaggedFile targetFile = files.stream()
                .filter(file -> file.getFilePath().equals(filePath))
                .findFirst()
                .orElse(null);

        if (targetFile == null) {
            System.out.println("Arquivo não encontrado, adicionando novo...");
            addFile(filePath);
            targetFile = files.stream()
                    .filter(file -> file.getFilePath().equals(filePath))
                    .findFirst()
                    .get();
        }

        targetFile.addTag(tag);
        System.out.println("Tag adicionada com sucesso!");
    }

    public List<TaggedFile> searchByTag(String tag) {
        String searchTerm = tag.toLowerCase();
        return files.stream()
                .filter(file -> file.getTags().stream()
                        .anyMatch(fileTag -> fileTag.getName().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    public List<TaggedFile> getFiles() {
        return files;
    }

    public void loadFromPersistence() {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        try (FileReader reader = new FileReader(PERSISTENCE_FILE)) {
            Type fileListType = new TypeToken<ArrayList<TaggedFile>>(){}.getType();
            List<TaggedFile> loadedFiles = gson.fromJson(reader, fileListType);
            if (loadedFiles != null) {
                this.files = loadedFiles;
            }
        } catch (IOException e) {
            this.files = new ArrayList<>();
            saveToPersistence();
        }
    }

    public void saveToPersistence() {
        System.out.println("Iniciando salvamento...");
        System.out.println("Caminho do arquivo: " + PERSISTENCE_FILE);
        System.out.println("Número de arquivos: " + files.size());
        
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
            
        try (FileWriter writer = new FileWriter(PERSISTENCE_FILE)) {
            String json = gson.toJson(files);
            //System.out.println("JSON gerado: " + json);
            writer.write(json);
            writer.flush();
            System.out.println("Arquivo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
