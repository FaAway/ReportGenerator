package ru.faraway.reportgenerator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.faraway.reportgenerator.settings.Column;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FarAway on 12.05.2016.
 *
 * Default charset: UTF-16
 */
public class ReportWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ReportWriter.class);

    private BufferedWriter writer;
    private Settings settings;
    private int currentLine = 0;
    private int currentPage = 0;

    public ReportWriter(String filename, Settings settings) {
        this(filename, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings);
    }

    public ReportWriter(String filename, Charset charset, Settings settings) {
        try {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), charset)) {
            @Override
            public void write(String s) {
                try {
                    super.write(s);
                } catch (IOException e) {
                    LOG.error("Can't write to: \"" + filename + "\"", e);
                    throw new RuntimeException("Can't write to: \"" + filename + "\"");
                }
                int lines = s.length() / settings.getPage().getWidth();
                currentLine += lines;
            }
        };
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException();
        }
        this.settings = settings;
    }

    public void writeFormattedDataLine(String[] dataLine) throws IOException {

        ArrayList<String[]> trimmedDataLines = spliceDataToFitColumnWidth(dataLine);

        if (!isThereEnoughSpaceFor(trimmedDataLines.size()))
            printPageSeparator();

        if (currentLine == 0)
            printHeader();

        printDottedLineSeparator();
        for (String[] trimmedDataLine : trimmedDataLines)
            printSingleLine(trimmedDataLine);
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            LOG.error("Can't close file", e);
        }
    }

    private boolean isThereEnoughSpaceFor(int linesCount) {
        return currentLine + linesCount + 1 <= settings.getPage().getHeight(); //  "+ 1" - height of DottedLineSeparator
    }

    // Cached vars. Header renders without last line separator at the end
    private String header = "";
    private int headerLinesCount;
    /*| header | header | header | header | header | header |\n*/
    private void printHeader() throws IOException {
        if (header.isEmpty()) {
            int columnsCount = settings.getColumns().size();
            String[] headerData = new String[columnsCount];
            for (int i = 0; i < columnsCount; i++) {
                headerData[i] = settings.getColumns().get(i).getTitle();
            }
            ArrayList<String[]> trimmedHeaderLines = spliceDataToFitColumnWidth(headerData);
            headerLinesCount = trimmedHeaderLines.size();
            for (int i = 0; i < trimmedHeaderLines.size(); i++) {
                header += renderSingleLine(trimmedHeaderLines.get(i));
                if (i < trimmedHeaderLines.size() - 1)
                    header += System.lineSeparator();
            }
        }
        writer.write(header);
    }

    // Cached var
    private String dottedLineSeparator = "";
    /* \n--------------------------------------------------------*/
    private void printDottedLineSeparator() throws IOException {
        if (dottedLineSeparator.isEmpty())
            dottedLineSeparator = StringUtils.leftPad("", settings.getPage().getWidth(), "-");
        writer.write(System.lineSeparator() + dottedLineSeparator);
    }

    /* \n| data   | data   | data   | data   | data   | data   |*/
    private void printSingleLine(String[] dataLine) throws IOException{
        writer.write(System.lineSeparator() + renderSingleLine(dataLine));
    }

    /* \n ~ \n                                                  */
    private void printPageSeparator() throws IOException {
        writer.write(System.lineSeparator() + "~" + System.lineSeparator());
        currentLine = 0;
        currentPage++;
    }

    private ArrayList<String[]> spliceDataToFitColumnWidth(String... dataLine) {

        ArrayList<String[]> trimmedDataLines = new ArrayList<>();

        List<Column> columns = settings.getColumns();
        boolean fitInSingleLine;
        do {
            fitInSingleLine = true; //assumption
            String[] trimmedDataLine = new String[columns.size()];
            String[] overheadDataLine = new String[columns.size()];

            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                String data = dataLine[i];
                if (data == null || data.length() <= column.getWidth())
                    trimmedDataLine[i] = data;
                else {
                    int cutIndex = getCutIndex(data, column.getWidth());
                    trimmedDataLine[i] = data.substring(0, cutIndex);
                    overheadDataLine[i] = data.substring(cutIndex).trim();
                    fitInSingleLine = !(overheadDataLine[i].length() > 0);
                }
            }
            trimmedDataLines.add(trimmedDataLine);
            if (!fitInSingleLine) dataLine = overheadDataLine;
        } while (!fitInSingleLine);
        return trimmedDataLines;
    }

    private int getCutIndex(String data, int width) {
        if (data.length() <= width) {
            LOG.error("Nothing to cut. There is enough width for data");
            throw new IllegalArgumentException("Nothing to cut. There is enough width for data");
        }

        String firstPart = data.substring(0, width);
        boolean wordCutted = data.substring(width - 1, width + 1).matches("\\w{2}");
        if (!wordCutted) return width;
        else {
            String s = firstPart.replaceAll("\\W"," ");
            int separator = s.lastIndexOf(" ");
            return separator != -1 ? separator + 1 : width;
        }
    }

    private String renderSingleLine(String[] dataLine) {
        StrBuilder textline = new StrBuilder("|");
        for (int i = 0; i < settings.getColumns().size(); i++) {
            Column column = settings.getColumns().get(i);
            String data = dataLine[i];
            if (data != null)
                textline.append(" " + StringUtils.rightPad(data, column.getWidth()) + " |");
            else textline.append(StringUtils.rightPad("", 1 + column.getWidth()) + " |");
        }
        return textline.toString();
    }
}
