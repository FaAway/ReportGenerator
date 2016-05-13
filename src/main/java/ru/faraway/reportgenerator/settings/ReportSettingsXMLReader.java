package ru.faraway.reportgenerator.settings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by FarAway on 12.05.2016.
 */
public class ReportSettingsXMLReader {
    public Settings unmarshall(InputStream xml) {
        Settings settings = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            settings = (Settings)unmarshaller.unmarshal(xml);


        } catch (JAXBException exception) {
            Logger.getLogger(ReportSettingsXMLReader.class.getName()).
                    log(Level.SEVERE, "unmarshall(InputStream xml) threw JAXBException", exception);
        }
        return settings;
    }
}
