package ru.faraway.reportgenerator.settings;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by FarAway on 12.05.2016.
 */

@XmlType(propOrder = {"title", "width"}, name = "column")
public class Column {
    private String title;
    private int width;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
