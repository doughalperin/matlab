package doug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Waves {

    static final int WAVES_PER_SPECTRUM = 2068;
    Map<String, double[]> values;

    Waves(String filename) throws IOException {
        FileReader reader = new FileReader(new File(filename));
        BufferedReader bufferedReader = new BufferedReader(reader);

        values = new HashMap<String, double[]>();

        int currentLine = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println("Read line#" + currentLine);
            //skip 1 header lines
            if (currentLine++ < 1) {
                continue;
            }
            lineToWaves(line);
        }
        bufferedReader.close();
    }

    void lineToWaves(String line) {
        String[] data = line.split(",");
        System.out.println(line);
        double[] wavelengths = new double[WAVES_PER_SPECTRUM];
        String index = data[0];
        for (int j = 0; j < WAVES_PER_SPECTRUM; j++) {
            System.out.print(j + " ");
            wavelengths[j] = Double.parseDouble(data[j + 1]);
        }

        values.put(index, wavelengths);
    }
}

