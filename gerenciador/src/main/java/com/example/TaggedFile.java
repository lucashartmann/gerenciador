package com.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TaggedFile {
    private String filePath;  // Alterado para String para facilitar a serialização
    private Set<FileTag> tags;

    public TaggedFile(Path filePath) {
        this.filePath = filePath.toString();  // Armazena como String
        this.tags = new HashSet<>();
    }

    public Path getFilePath() {
        return Paths.get(filePath);  // Converte de volta para Path
    }

    public Set<FileTag> getTags() {
        return tags;
    }

    public void addTag(FileTag tag) {
        tags.add(tag);
    }

    public void removeTag(FileTag tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getName().equals(tagName));
    }

    @Override
    public String toString() {
        return Paths.get(filePath).getFileName().toString();  // Converte para Path antes de obter o nome do arquivo
    }
}
