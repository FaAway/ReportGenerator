package ru.faraway.reportgenerator.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by FarAway on 12.05.2016.
 *
 *
 */
public class ReportSettingsXMLReader {
    private static final Logger LOG = LoggerFactory.getLogger(ReportSettingsXMLReader.class);

    public static Settings getSettings(String settingsPathName) {

        try (FileInputStream input = new FileInputStream(settingsPathName)) {
            Settings settings = unmarshall(input);
            if (validateSettings(settings, Settings.ALLOW_ADAPTIVE_COLUMN_WIDTH))
                return settings;
            else
                throw new RuntimeException("Settings are incorrect");
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

    private static Settings unmarshall(InputStream xml) {

        Settings settings = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            settings = (Settings)unmarshaller.unmarshal(xml);
        }

        catch (JAXBException exception) {
            LOG.error("unmarshall(InputStream xml) threw JAXBException", exception);
        }

        return settings;
    }

    private static boolean validateSettings(Settings settings, boolean allowAdaptiveColumnWidth) {

        boolean isValidationPassed = true;

        int overallWidth = 0;
        for (Column column : settings.getColumns()) overallWidth += column.getWidth();
        overallWidth += settings.getColumns().size() * 3 + 1;

        if (settings.getPage().getWidth() != overallWidth) {
            LOG.info(String.format("Page has incorrect width: %d. It should be %d", settings.getPage().getWidth(), overallWidth));

            if (!allowAdaptiveColumnWidth)
                isValidationPassed = false;
            else {
                LOG.info(String.format("Page has inappropriate width: %d. Columns will be resized.", settings.getPage().getWidth()));
                isValidationPassed = adjustColumnsWidth(settings, 1.0 * settings.getPage().getWidth() / overallWidth);
            }
        }

        return isValidationPassed;
    }

    private static boolean adjustColumnsWidth(Settings settings, double resizeFactor) {

        boolean adjustIsPossible = true;

        int newOverallWidth = 0;
        for (int i = 0; i < settings.getColumns().size(); i++) {
            Column column = settings.getColumns().get(i);

            int newWidth = (int)(column.getWidth() * resizeFactor);

            if (newWidth > 1) {
                column.setWidth(newWidth);
                newOverallWidth += newWidth + 3;
            } else {
                LOG.info(String.format("Page with is too short: %d.", settings.getPage().getWidth()));
                adjustIsPossible = false;
                break;
            }

            // adjust width of the last column
            if (i == settings.getColumns().size() - 1) {
                newOverallWidth += 1;
                column.setWidth(column.getWidth() + settings.getPage().getWidth() - newOverallWidth);
            }
        }

        return adjustIsPossible;
    }
}
