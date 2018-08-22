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
import java.util.Arrays;
import java.util.HashMap;

public class MatFileMaker {

    enum PartType {
        ALL(""), PARTS("Part");
        String extension;

        PartType(String extension) {
            this.extension = extension;
        }

        static PartType getDefault() {
            return ALL;
        }
    }

    Waves waves;
    Data data;
    HashMap<Field, Object> output;

    public static void main(String[] args) throws Exception {

        String inputFile = args.length > 0 ? args[0] : "/Users/admin/Desktop/PB16N/PB16_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/Lines1000_19bin_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/Lines20019bin_cal.csv";
        inputFile = "/Users/admin/Desktop/testsetFULL19bin.csv";
        inputFile = "/Users/admin/Desktop/PB16N/randomset.csv";
        inputFile = "/Users/admin/Desktop/PB16N/PB1600852L_PB1600991G_PB1601182F_PB1612367.csv";
        inputFile =  "/Users/admin/Desktop/PB16N/PB16_cal.csv";
        inputFile =  "/Users/admin/perl/YAML/2017-10-23October-19bin/out/inter/";
        //inputFile += "addTraining19bin_calM.csv";
        //inputFile += "origOthersTraining19bin_calM.csv";
        inputFile += "testingOnly19bin_calM.csv";
        //inputFile = "/Users/admin/Desktop/PB16N/Lines1000_19bin_cal.csv";
        //inputFile = "/Users/admin/perl/YAML/PrelimCohortBData/PRELIM_cal.csv";
        inputFile = "/Users/admin/perl/YAML/PrelimCohortBData/PRELIM-1000101_6_cal.csv";
        inputFile = "/Users/admin/Desktop/PRB/DataProjects/GunderFish/T-10-baseTraining19bin_calM.csv";
        inputFile = "/Users/admin/Desktop/PB16N/PB1614789_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/June5ExcisedCohortB_uncal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/654F_3001_3301_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/Off_by_1_cal.csv";
        inputFile = "/Users/admin/Desktop/PB16N/T-baseTraining19bin_calM.csv";
        inputFile = "/Users/admin/perl/YAML/June5ExcisedProstateAndPrelim/June27TRUS/PrelimJune27TRUS_uncal.csv";
        inputFile = "/Users/admin/perl/YAML/2017-10-23October-19bin/out/inter/baseTraining19bin_uncalM.csv";
        inputFile = "/Users/admin/perl/YAML/RollInCohortB/July18TRUS/July18TRUS_cal.csv";
        inputFile = "/Users/admin/Desktop/PRB/DataProjects/GunderFish/109Transform/Transform_109Space.csv";
        inputFile = "/Users/admin/perl/YAML/June5ExcisedProstateAndPrelim/Site13NYUAug6PlusConsole6FromTUCC/Console6_cal.csv";
        inputFile = "/Users/admin/perl/YAML/RollInCohortB/FilteredJune17-July11RRP/RRP-3Filtered_cal.csv";
        inputFile = "/Users/admin/perl/YAML/2017-10-12October-FinalSet/OrigTrainingTRUSONLY/origTrainingTRUS_MR_cal.csv";
        inputFile = "/Users/admin/perl/YAML/Splitter/out/July18TRUS_cal_MSC.csv";
        inputFile = "/Users/admin/perl/YAML/2017-10-23October-19bin/out/inter/ValidationTestOnly-addTrain_cal.csv";

        String outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout.mat";
        outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout1000.mat";
        outputFile = args.length > 1 ? args[1] : "/Users/admin/Desktop/testout200.mat";
        outputFile = "/Users/admin/Desktop/testFULL.mat";
        outputFile = "/Users/admin/Desktop/PB16N/RandomFull.mat";
        int inx = inputFile.lastIndexOf("/");
        outputFile = "/Users/admin/Desktop/PB16N/"
            + inputFile.substring(inx + 1).replace(".csv", ".mat"); //"Part5.mat");
        //outputFile = "/Users/admin/Desktop/PB16N/temp.mat";
        //outputFile = "/Users/admin/Desktop/PB16N/PRELIM_cal.mat";

        String waveFile = "/Users/admin/Desktop/PB16N/CohortA_waves.csv";
        waveFile = "/Users/admin/Desktop/PB16N/All_waves.csv";  //include CohortB waves

        String[] includeList = { //baseTrainingMRNs
                "05005RHO", "05004FMD", "05003GA", "05002FM", "03024JTA", "03016MJG", "03013TDK", "03012TG", "03011DEM",
                "03010BCK", "03003JV", "99030RJ", "99029JS", "99028DP", "99026TH", "99024KJ", "99022JPN", "02043TGN",
                "02042RMS", "02041CWS", "02040CGR", "02039DRH", "02036DJS", "02035RPM", "02033CTM", "02032DEM", "02031DES",
                "02030DER", "02028GLP", "02026SEB", "02025RLC", "02022MWC", "02021RFD", "02016CWC", "02014CMJ",
                "02013SHC", "02011CRV", "02009ARC", "02007GJB", "02006HSD", "02005MSK", "02003DMC", "02002DAB",
                "01006GAG", "01005JLD", "01004RJD", "01003LKG", "01002GRV", "01001LLT", "07011GB", "07010PB",
                "07008NB", "07006HK", "07005JEB", "06009DLT", "06007AB", "06004HJK", "06001WAR", "04024JWH",
                "04022RMB", "04013AJM", "04012GAC", "04011GNG", "04010CEM", "04009FF", "04008WDC", "04007JWH",
                "04006TES", "04004WSW", "04003DFL", "04001CJM", "03026RTT", "03023HAD", "03019PJK", "03018WR",
                "03009DDB", "03006SPM", "03005DSP", "03004DML", "03002PSV", "03001SJO"};
        includeList = null;

        /*testMLCell(outputFile);
        System.exit(1);*/

        PartType partType = args.length > 2 ? PartType.valueOf(args[2]) : PartType.getDefault();
        partType= PartType.PARTS; //include this line to build in parts

        System.out.println("Making a matfile from " + inputFile + " to " + outputFile + " of PartTyoe:" + partType);
        MatFileMaker maker = new MatFileMaker(waveFile, inputFile);

        DataLine dl;
        DataLine priorDataLine = null;
        while ((dl = maker.data.nextLine()) != null) {
            System.out.println("Read a line:" + dl.hash.get("core_id") + "-" + dl.hash.get("bin_index"));
            //magic mrn list skipper
            if (includeList == null || Arrays.asList(includeList).contains(dl.hash.get("mrn"))) {
                //put into place
                for (Field field : Field.fields) {
                    field.putValue(maker.output.get(field), dl, maker.waves, priorDataLine);
                }
            }
            priorDataLine = dl; //remember for next time
        }
        if (partType == PartType.ALL) {
            //now output everything
            maker.writeFile(outputFile, null);
        } else { //parts
            int lastIndex = outputFile.lastIndexOf(".");
            Field.Part[] parts = Field.Part.values();
            for (int i = 0; i < parts.length; i++) {
                //make names 1 based to match Matlab
                maker.writeFile( lastIndex == -1
                        ? outputFile + PartType.PARTS.extension + (i + 1)
                        : outputFile.substring(0, lastIndex) + PartType.PARTS.extension + (i + 1) + outputFile.substring(lastIndex),
                    parts[i]);
            }

        }
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
    void writeFile(String outputFile, Field.Part part) throws IOException {
        System.out.println("Writing to file " + outputFile + (part == null ? "" : " for part=" + part));

        ArrayList<MLArray> list = new ArrayList<MLArray>();

        for (Field field : Field.fields) {
            //IF ALL, only PART1 or null IELSE specific part
            if ((part == null && (field.part != Field.Part.PART1 && field.part != null))
                    || (part != null && part != field.part)) {
                continue;
            }
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