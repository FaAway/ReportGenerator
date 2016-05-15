package ru.faraway.reportgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.faraway.reportgenerator.settings.Column;
import ru.faraway.reportgenerator.settings.Settings;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by FarAway on 12.05.2016.
 *
 * Default charset: UTF-16
 */
public class TsvReader {

    private static final Logger LOG = LoggerFactory.getLogger(TsvReader.class);

    private BufferedReader reader;
    private List<Column> columns;
    private String nextLine;
    private int linesRead;

    public TsvReader(InputStreamReader in, List<Column> columns) throws IOException{

        reader = new BufferedReader(in) {
            @Override
            public String readLine() throws IOException {
                String line = null;
                try {
                    line = super.readLine();
                } catch (IOException e) { e.printStackTrace(); }
                if (line != null) linesRead++;
                return line;
            }
        };
        this.columns = columns;

        if (!readNextLine()) {
            LOG.error("File is empty");
            throw new EOFException("File is empty");
        }
    }

    public TsvReader(String fileName, List<Column> columns) throws IOException{

        this(new InputStreamReader(new FileInputStream(fileName), Settings.DEFAULT_CHARSET_NAME), columns);
    }

    public TsvReader(String fileName, Charset charset, List<Column> columns) throws IOException {
        this(new InputStreamReader(new FileInputStream(fileName), charset), columns);
    }

    public boolean available() {
        return nextLine != null || readNextLine();
    }

    private boolean readNextLine() {
        try { nextLine = reader.readLine(); } catch (IOException e) { /*cached in class wrapper*/ }
        return (nextLine != null);
    }

    public int getLinesRead() {
        return linesRead;
    }

    public String[] readDataLine()  {
        if (nextLine == null) readNextLine();
        String[] result = nextLine.split("\t");
        nextLine = null;

        if (result.length != columns.size()) {
            String errMessage = String.format("Expected %d columns but found %d in line %d", columns.size(), result.length, linesRead);
            LOG.error(errMessage);
            throw new RuntimeException(errMessage);
        }

        return result;
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            LOG.error("Can't close file", e);
        }
    }
}
