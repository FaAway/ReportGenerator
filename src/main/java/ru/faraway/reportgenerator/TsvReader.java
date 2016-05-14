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
    private int linesReaded;

    public TsvReader(InputStreamReader in, List<Column> columns) throws IOException{
        reader = new BufferedReader(in);
        this.columns = columns;
        if (!readNextLine()) {
            LOG.error("File is empty");
            throw new EOFException("File is empty");
        }
    }

    public TsvReader(String fileName, List<Column> columns) throws IOException{
        this(new InputStreamReader(new FileInputStream(fileName), Settings.DEFAULT_CHARSET_NAME), columns);
    }

    public TsvReader(String fileName, Charset charset, List<Column> columns) throws IOException{
        this(new InputStreamReader(new FileInputStream(fileName), charset), columns);
    }

    public boolean available() {
        return nextLine != null ? true : readNextLine();
    }

    private boolean readNextLine() {
        try {
            nextLine = reader.readLine();
        } catch (IOException e) { e.printStackTrace(); }
        if (nextLine != null) {
            linesReaded++;
            return true;
        } else return false;
    }

    public int getLinesReaded() {
        return linesReaded;
    }

    public String[] readDataLine()  {
        if (nextLine == null) readNextLine();
        String[] result = nextLine.split("\t");
        if (result.length != columns.size()) {
            String errMessage = String.format("Expected %d columns but found %d in line %d", columns.size(), result.length, linesReaded);
            LOG.error(errMessage);
            throw new RuntimeException(errMessage);
        }
        nextLine = null;
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
