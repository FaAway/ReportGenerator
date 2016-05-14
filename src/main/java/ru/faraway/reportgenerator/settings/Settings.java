package ru.faraway.reportgenerator.settings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FarAway on 12.05.2016.
 *
 * Java bean for XML loading and some constants
 */

@XmlType(propOrder = { "page", "columns" }, name = "settings")
@XmlRootElement
public class Settings {
    public static final String DEFAULT_CHARSET_NAME = "UTF-16";
    public static final boolean ALLOW_ADAPTIVE_COLUMN_WIDTH = true;

    private Page page;

    private List<Column> columns = new ArrayList<>();

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @XmlElementWrapper(name = "columns")
    @XmlElement(name = "column")
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Objects created from XML:\n")
          .append(String.format("Width: %s, Height: %s\n", this.getPage().getWidth(), this.getPage().getHeight()));
        for (Column column : this.getColumns()) {
            sb.append("[\n")
              .append("Title: " + column.getTitle() + "\n")
              .append("Width: " + column.getWidth() + "\n")
              .append("]\n");
        }
        return sb.toString();
    }
}


