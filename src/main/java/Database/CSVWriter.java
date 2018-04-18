package Database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CSVWriter {
    private final FileWriter writer;

    /**
     * Creates a new Database.CSVWriter to write to the file with the given name
     * @param fileName name of the CSV file
     */
    public CSVWriter(String fileName) {
        try {
            writer = new FileWriter(fileName, false);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid file path");
        }
    }

    public void writeRS(ResultSet rs, int columns) {
        try {
            while (rs.next()) {
                Object[] list = new Object[columns];
                for (int i = 1; i <= columns; i++) {
                    list[i-1] = rs.getObject(i);
                }
                writeLine(list);
            }
            writer.close();
        } catch (SQLException e) {
            System.out.println("Problem with SQL code");
        }catch(IOException e){

        }

    }

    /**
     * Writes a single line to the file
     * @param list List of objects to write to file
     */
    public void writeLine(Object[] list) {
        String str = "";
        for (Object o : list) {
            str += o.toString() + ",";
        }
        str = str.substring(0, str.length()-1);
        try {
            writer.write(str + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Could not write line to CSV");
        }
    }

    /**
     * Writes a single line to the CSV file
     * @param list ArrayList of objects to write to the file
     */
    public void writeLine(ArrayList<Object> list) {
        writeLine(list.toArray());
    }

    /**
     * Writes a single line to the CSV file
     * @param o objects to write to the file
     */
    public void writeLineLong(Object... o) {
        writeLine(o);
    }
}
