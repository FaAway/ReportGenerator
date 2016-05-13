package ru.faraway.reportgenerator;

import ru.faraway.reportgenerator.settings.ReportSettingsXMLReader;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

/**
 * Created by FarAway on 12.05.2016.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        String settingsPathName = args[0];
        Settings settings = getSettings(settingsPathName);
        //System.out.println(settings.toString());

        String sourceDataPathName = args[1];
        TsvReader tsvReader = new TsvReader(sourceDataPathName, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings.getColumns());

        String outputReportPathName = args[2];
        ReportWriter reportWriter = new ReportWriter(outputReportPathName, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings);

        while (tsvReader.available()) {
            reportWriter.writeNext(tsvReader.readDataLine());
        }

        tsvReader.close();
        reportWriter.close();
    }

    private static Settings getSettings(String settingsPathName) throws FileNotFoundException {ReportSettingsXMLReader xmlReader = new ReportSettingsXMLReader();
        FileInputStream input = new FileInputStream(settingsPathName);
        return xmlReader.unmarshall(input);
    }
}
