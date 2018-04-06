package doug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Data {

    private BufferedReader bufferedReader;
    private int currentLine;

    Data(String filename) throws IOException {
        FileReader reader = new FileReader(new File(filename));
        bufferedReader = new BufferedReader(reader);

        currentLine = 0;
    }

    DataLine nextLine() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println("Read line#" + currentLine);
            //skip 2 header lines
            if (currentLine++ >= 2) {
                break;
            }
        }
        if (line == null) {
            bufferedReader.close();
            return null;
        }
        return new DataLine(line);
    }
}

