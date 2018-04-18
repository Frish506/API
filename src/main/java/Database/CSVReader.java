package Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReader {
    private final FileReader reader;
    private final String separator;

    /**
     * Initialize the Database.Database.CSVReader class
     *
     * @param reader FileReader with CSV file open
     * @param separator String between items in the CSV
     */
    public CSVReader (FileReader reader, String separator){
        this.reader = reader;
        this.separator = separator;
    }

    /**
     * Reads all lines from a CSV file and stores in an ArrayList of String[]s
     *
     * @return ArrayList<String[]> with all lines in the csv file
     * @throws IOException Can through error from BufferedReader.readLine
     */
    public ArrayList<String[]> readCSV() throws IOException {
        //variable declaration
        ArrayList<String[]> data = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(this.reader);
        String line;

        /*
         * Loop through lines within the .csv file
         * Split each line by comma
         * add the data points into the data arrayList
         */
         boolean first = true;
        while((line = bufferedReader.readLine()) != null) {
            //if(first){
              //  first = false;
            //}
            //else {
                String[] dataLine = line.split(this.separator);
                data.add(dataLine);
            //}
        }
        return data;
    }
}
