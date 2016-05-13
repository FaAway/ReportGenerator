package ru.faraway.reportgenerator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
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

    private BufferedWriter writer;
    private Settings settings;
    // Header renders without last line separator
    private String header = "";
    private String dottedLineSeparator = "";
    private int headerLinesCount;
    //start for 0
    private int currentLine;
    //start for 0
    private int currentPage;

    public ReportWriter(String filename, Settings settings) throws IOException{
        this(filename, Charset.forName(Settings.DEFAULT_CHARSET_NAME), settings);
    }

    public ReportWriter(String filename, Charset charset, Settings settings) throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), charset)) {
            @Override
            public void write(String s) throws IOException {
                super.write(s);
                int lines = s.length() / settings.getPage().getWidth();
                currentLine += lines;
            }
        };
        this.settings = settings;
        renderHeader();
        renderDottedLineSeparator();
    }

    private void renderHeader() {
        if (header.isEmpty()) {
            int columnsCount = settings.getColumns().size();
            String[] headerData = new String[columnsCount];
            for (int i = 0; i < columnsCount; i++) {
                headerData[i] = settings.getColumns().get(i).getTitle();
            }
            ArrayList<String[]> trimmedHeaderLines = spliceDataToFitWidth(headerData);
            headerLinesCount = trimmedHeaderLines.size();
            for (int i = 0; i < trimmedHeaderLines.size(); i++) {
                header += renderSingleLine(trimmedHeaderLines.get(i));
                if (i < trimmedHeaderLines.size() - 1)
                    header += System.lineSeparator();
            }
        }
    }

    private void renderDottedLineSeparator() {
        dottedLineSeparator = StringUtils.leftPad("", settings.getPage().getWidth(), "-");
    }

    public void writeNext(String[] dataLine) {
        try {
            ArrayList<String[]> trimmedDataLines = spliceDataToFitWidth(dataLine);
            if (trimmedDataLines.size()+ 1 + currentLine > settings.getPage().getHeight()) {
                writer.write(System.lineSeparator() + "~" + System.lineSeparator());
                currentLine = 0;
                currentPage++;
            }

            if (currentLine == 0)
                writer.write(header);

            writer.write(System.lineSeparator() + dottedLineSeparator);
            for (String[] trimmedDataLine : trimmedDataLines) {
                writer.write(System.lineSeparator() + renderSingleLine(trimmedDataLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String[]> spliceDataToFitWidth(String... dataLine) {

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
                    fitInSingleLine = false;
                    int cutpoint = getCutIndex(data, column.getWidth());
                    trimmedDataLine[i] = data.substring(0, cutpoint);
                    overheadDataLine[i] = data.substring(cutpoint).trim();
                }
            }
            trimmedDataLines.add(trimmedDataLine);
            if (!fitInSingleLine) dataLine = overheadDataLine;
        } while (!fitInSingleLine);
        return trimmedDataLines;
    }

    private int getCutIndex(String data, int width) {
        if (data.length() <= width) throw new IllegalArgumentException("Nothing to cut. There is enough width for data");
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

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
