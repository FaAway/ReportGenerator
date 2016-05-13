package ru.faraway.reportgenerator.settings;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by FarAway on 12.05.2016.
 */
@XmlType(propOrder = {"width", "height"}, name = "page")
public class Page {
    private int width;
    private int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
