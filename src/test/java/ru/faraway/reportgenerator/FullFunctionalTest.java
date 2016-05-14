package ru.faraway.reportgenerator;

import org.apache.commons.lang3.text.StrBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static ru.faraway.reportgenerator.testdata.TestData.*;

/**
 * Created by FarAway on 14.05.2016.
 */
public class FullFunctionalTest {
    private static TsvReader tsvReader;
    private static ReportWriter reportWriter;
    private static Path testReportPath;
    private static Path expectedReportPath;

    @BeforeClass
    public static void setup() throws IOException {
        System.out.println("Files to compare:");
        testReportPath = Files.createTempFile(testReportFileName, ".tmp");
        System.out.println(testReportPath.toString());
        expectedReportPath = Files.createTempFile(expectedReportFileName, ".tmp");
        System.out.println(expectedReportPath.toString());

        InputStream expectedReportStream = FullFunctionalTest.class.getResourceAsStream("/" + expectedReportFileName);
        Files.copy(expectedReportStream, expectedReportPath, StandardCopyOption.REPLACE_EXISTING);
        expectedReportStream.close();

        makeReport();
    }

    public static void makeReport() throws IOException{

        InputStream testDataStream = FullFunctionalTest.class.getResourceAsStream("/source-data.tsv");
        tsvReader = new TsvReader(new InputStreamReader(testDataStream, Settings.DEFAULT_CHARSET_NAME), getSETTINGS().getColumns());
        reportWriter = new ReportWriter(testReportPath.toString(), getSETTINGS());

        while (tsvReader.available()) {
            reportWriter.writeFormattedDataLine(tsvReader.readDataLine());
        }
        tsvReader.close();
        reportWriter.close();
    }

    @Test
    public void compareReports() throws IOException{
        BufferedReader resultReader = new BufferedReader(new InputStreamReader(new FileInputStream(testReportPath.toString()), Settings.DEFAULT_CHARSET_NAME));
        StrBuilder resultedBldr = new StrBuilder();
        resultReader.lines().forEach(resultedBldr::append);
        resultReader.close();

        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(new FileInputStream(expectedReportPath.toString()), Settings.DEFAULT_CHARSET_NAME));
        StrBuilder expectedBldr = new StrBuilder();
        expectedReader.lines().forEach(expectedBldr::append);
        expectedReader.close();

        System.out.println("Expected: " + expectedBldr.toString());
        System.out.println("Resulted: " + resultedBldr.toString());
        Assert.assertArrayEquals(expectedBldr.toCharArray(), resultedBldr.toCharArray());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        tsvReader = null;
        reportWriter = null;
        Files.delete(expectedReportPath);
        Files.delete(testReportPath);
    }
}
