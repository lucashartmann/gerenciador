package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileTagManager {
    private List<TaggedFile> files;
    private static final String PERSISTENCE_FILE = "file_tags.json";

    public FileTagManager() {
        this.files = new ArrayList<>();
    }

    public void addFile(Path filePath) {
        files.add(new TaggedFile(filePath));
    }

    public void addTagToFile(Path filePath, FileTag tag) {
        for (TaggedFile file : files) {
            if (file.getFilePath().equals(filePath)) {
                file.addTag(tag);
                return;
            }
        }
    }

    public List<TaggedFile> searchByTag(String tag) {
        return files.stream()
                .filter(file -> file.hasTag(tag))
                .collect(Collectors.toList());
    }

    public List<TaggedFile> getFiles() {
        return files;
    }

    public void loadFromPersistence() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(PERSISTENCE_FILE)) {
            Type fileListType = new TypeToken<ArrayList<TaggedFile>>(){}.getType();
            List<TaggedFile> loadedFiles = gson.fromJson(reader, fileListType);
            if (loadedFiles != null) {
                this.files = loadedFiles;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToPersistence() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(PERSISTENCE_FILE)) {
            gson.toJson(this.files, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
