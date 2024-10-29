package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class TaggedFile {
    private String filePath;
    private Set<FileTag> tags;
    private boolean isDirectory;

    public TaggedFile() {
        this.tags = new HashSet<>();
    }

    public TaggedFile(Path filePath) {
        this.filePath = filePath.toString();
        this.tags = new HashSet<>();
        this.isDirectory = Files.isDirectory(filePath);
    }

    public Path getFilePath() {
        return Paths.get(filePath);
    }

    public String getFilePathString() {
        return filePath;
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

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String toString() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaggedFile that = (TaggedFile) o;
        return filePath.equals(that.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}
