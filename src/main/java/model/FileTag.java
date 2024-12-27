package model;

import javafx.scene.paint.Color;

public class FileTag {
    private String name;
    private String color;

    // Construtor vazio para o Gson
    public FileTag() {
    }

    public FileTag(String name, Color color) {
        this.name = name;
        this.color = convertColorToString(color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return Color.web(color);
    }

    public void setColor(Color color) {
        this.color = convertColorToString(color);
    }

    private String convertColorToString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @Override
    public String toString() {
        return name;
    }
}
