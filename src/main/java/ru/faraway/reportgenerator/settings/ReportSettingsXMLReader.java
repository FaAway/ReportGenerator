package ru.faraway.reportgenerator.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by FarAway on 12.05.2016.
 */
public class ReportSettingsXMLReader {
    private static final Logger LOG = LoggerFactory.getLogger(ReportSettingsXMLReader.class);

    public Settings unmarshall(InputStream xml) {
        Settings settings = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            settings = (Settings)unmarshaller.unmarshal(xml);


        } catch (JAXBException exception) {
            LOG.error("unmarshall(InputStream xml) threw JAXBException", exception);
        }
        return settings;
    }
}
