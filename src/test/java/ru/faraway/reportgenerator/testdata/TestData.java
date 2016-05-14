package ru.faraway.reportgenerator.testdata;

import ru.faraway.reportgenerator.settings.Column;
import ru.faraway.reportgenerator.settings.Page;
import ru.faraway.reportgenerator.settings.Settings;

import java.util.ArrayList;

/**
 * Created by FarAway on 13.05.2016.
 */
public class TestData {
    public static final String testReportFileName = "test-report.txt";
    public static final String expectedReportFileName = "expected-report.txt";

    private static Settings SETTINGS;

    public static Settings getSETTINGS() {
        if (SETTINGS == null) {
            SETTINGS = new Settings();
            Page page = new Page();
            page.setWidth(32);
            page.setHeight(12);
            SETTINGS.setPage(page);
            Column colum1 = new Column();
            colum1.setTitle("Номер");
            colum1.setWidth(8);
            Column colum2 = new Column();
            colum2.setTitle("Дата");
            colum2.setWidth(7);
            Column colum3 = new Column();
            colum3.setTitle("ФИО");
            colum3.setWidth(7);
            ArrayList<Column> columns = new ArrayList<>();
            columns.add(colum1);
            columns.add(colum2);
            columns.add(colum3);
            SETTINGS.setColumns(columns);
        }
        return SETTINGS;
    }
}
