package ru.faraway.reportgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.faraway.reportgenerator.settings.ReportSettingsXMLReader;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by FarAway on 12.05.2016.
 *
 * Main class.
 * Program arguments:
 * 1) XML file with setting for report printing
 * 2) Source file with TAB-delimited data
 * 3) Report filename for output
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            LOG.error("Please provide 3 program arguments. For example: settings.xml source-data.tsv example-report.txt");
            return;
        }

        String settingsPathName = args[0];
        Settings settings = ReportSettingsXMLReader.getSettings(settingsPathName);

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


}
