package ru.faraway.reportgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.faraway.reportgenerator.settings.ReportSettingsXMLReader;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by FarAway on 12.05.2016.
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException{
        String settingsPathName = args[0];
        Settings settings = getSettings(settingsPathName);

        String sourceDataPathName = args[1];
        TsvReader tsvReader = new TsvReader(sourceDataPathName, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings.getColumns());

        String outputReportPathName = args[2];
        ReportWriter reportWriter = new ReportWriter(outputReportPathName, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings);

        while (tsvReader.available()) {
            reportWriter.writeFormattedDataLine(tsvReader.readDataLine());
        }

        tsvReader.close();
        reportWriter.close();
    }

    private static Settings getSettings(String settingsPathName) {
        ReportSettingsXMLReader xmlReader = new ReportSettingsXMLReader();
        try (FileInputStream input = new FileInputStream(settingsPathName)) {
            return xmlReader.unmarshall(input);
        }
        catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException();
        }
        catch (IOException e) {
            LOG.error("Can't read settings", e);
            throw new RuntimeException("Can't read settings");
        }
    }
}
