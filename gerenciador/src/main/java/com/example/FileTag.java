package com.example;

import javafx.scene.paint.Color;

public class FileTag {
    private String name;
    private String color;  // Armazena a cor como String (HEX)

    public FileTag(String name, Color color) {
        this.name = name;
        this.color = color.toString();  // Converte a cor para String
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return Color.valueOf(color);  // Converte de volta para Color
    }

    @Override
    public String toString() {
        return name;
    }
}
