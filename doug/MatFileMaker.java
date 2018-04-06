package doug;

import com.jmatio.io.MatFile;
import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MatFileMaker {

    Waves waves;
    Data data;
    HashMap<Field, Object> output;

    public static void main(String[] args) throws Exception {

        String inputFile = args.length > 0 ? args[0] : "/Users/admin/Desktop/PB16N/PB16_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/Lines1000_19bin_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/Lines20019bin_cal.csv";
        inputFile = "/Users/admin/Desktop/testsetFULL19bin.csv";

        String outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout.mat";
        outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout1000.mat";
        outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout200.mat";
        outputFile = "/Users/admin/Desktop/testFULL.mat";
        String waveFile = "/Users/admin/Desktop/PB16N/CohortA_waves.csv";

        /*testMLCell(outputFile);
        System.exit(1);*/

        System.out.println("Making a matfile from " + inputFile + " to " + outputFile);
        MatFileMaker maker = new MatFileMaker(waveFile, inputFile);

        DataLine dl;
        DataLine priorDataLine = null;
        while ((dl = maker.data.nextLine()) != null) {
            System.out.println("Read a line:" + dl.hash.get("core_id") + dl.hash.get("bin_index"));
            //put into place
            for (Field field : Field.fields) {
                field.putValue(maker.output.get(field), dl, maker.waves, priorDataLine);
            }
            priorDataLine = dl; //remember for next time
        }
        //now output everything
        maker.writeFile(outputFile);
    }

    MatFileMaker(String waveFile, String inputFile) throws IOException {
        waves = new Waves(waveFile);
        data = new Data(inputFile);
        output = new HashMap<Field, Object>();
        for (Field field : Field.fields) {
            output.put(field, field.getObject());
        }
    }

    @SuppressWarnings("unchecked")
    void writeFile(String outputFile) throws IOException {

        ArrayList<MLArray> list = new ArrayList<MLArray>();

        for (Field field : Field.fields) {
            MLArray out = field.makeOutput(output.get(field));
            if (out == null) {
                continue;
            }
            list.add(out);
        }
        // write arrays to file
        new MatFileWriter(outputFile, list);

    }


    public static void testBenchmarkDouble() throws Exception {
        final String fileName = "bb.mat";
        final String name = "bigdouble";
        final int SIZE = 1000;

        MLDouble mlDouble = new MLDouble(name, new int[]{SIZE, SIZE});

        for (int i = 0; i < SIZE * SIZE; i++) {
            mlDouble.set((double) i, i);
        }

        // write array to file
        ArrayList<MLArray> list = new ArrayList<MLArray>();
        list.add(mlDouble);

        // write arrays to file
        new MatFileWriter(fileName, list);

    }

    static public void testWritingManyArraysInFile() throws IOException {
        final String fileName = "bb.mat";

        // test column-packed vector
        double[] src = new double[]{1.3, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] src2 = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] src3 = new double[]{3.1415};

        // create 3x2 double matrix
        // [ 1.0 4.0 ;
        // 2.0 5.0 ;
        // 3.0 6.0 ]
        MLDouble m1 = new MLDouble("m1", src, 3);
        MLDouble m2 = new MLDouble("m2", src2, 3);
        MLDouble m3 = new MLDouble("m3", src3, 1);
        // write array to file
        ArrayList<MLArray> list = new ArrayList<MLArray>();
        list.add(m1);
        list.add(m2);
        list.add(m3);

        // write arrays to file
        new MatFileWriter(fileName, list);

    }

    static public void testMLCell(String fileName) throws IOException {
        // array name
        String name = "doublearr";
        String name2 = "name";

        // test column-packed vector
        double[] src = new double[]{1.3, 2.0, 3.0, 4.0, 5.0, 6.0};

        // create 3x2 double matrix
        // [ 1.0 4.0 ;
        // 2.0 5.0 ;
        // 3.0 6.0 ]
        MLDouble mlDouble = new MLDouble(name, src, 3);
        MLChar mlChar = new MLChar(name2, "none");

        MLCell mlCell = new MLCell("cl", new int[]{2, 1});
        mlCell.set(mlChar, 0);
        mlCell.set(mlDouble, 1);

        // write array to file
        ArrayList<MLArray> list = new ArrayList<MLArray>();
        list.add(mlCell);

        // write arrays to file
        new MatFileWriter(fileName, list);

    }


}