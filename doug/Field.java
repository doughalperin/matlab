package doug;

import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import java.util.ArrayList;

public class Field {

    static Field[] fields = {
            new Field("bin_gleason_score", Type.DOUBLE, "gleason_score"),
            new Field("bin_index", Type.DOUBLE, "bin_index"),
            new Field("check_flag", Type.CELL, "check_flag"),
            new Field("core_gleason_score", Type.DOUBLE,"core_gleason", "core_id"),  //core level
            new Field("core_id", Type.CELL, "core_id"),
            new Field("I_ts", Type.DOUBLE_SPECTRA,
                    "spectral280nm,spectral340nm,spectrabroadband,darkspectra"),  //uses spectra
            new Field("ivv_bin", Type.DOUBLE, "id"),
            new Field("ivv_core", Type.DOUBLE_TRANSPOSED, null,  "core_id"), //UNCLEAR..appears unused
            new Field("ivv_validIndex", Type.DOUBLE, null), //UNCLEAR..appears unused
            new Field("label", Type.DOUBLE, "bin_label"),
            new Field("label_core", Type.DOUBLE, "core_label"),
            new Field("mrn", Type.CELL, "mrn"),
            new Field("procedure", Type.CELL,   "procedure"),
            new Field("result", Type.NULL, null), //SKIP
            new Field("site", Type.CELL, "site"),
            new Field("spectrometer", Type.CELL, "spectrometer_id"),
            new Field("ste", Type.CELL, "site"), //should be limited to valid only
            new Field("valid_index_core", Type.DOUBLE, "id", "core_id"),  //core level;UNCLEAR
            new Field("validIndex", Type.DOUBLE_TRANSPOSED, "id"),  //UNCLEAR (generated in apply_signal_processing
                        //should be limited to just good ones; for now generate all
            new Field("WVs_ts", Type.DOUBLE_ARRAY, "wave_index")
    };

    enum Type {DOUBLE, CELL, DOUBLE_TRANSPOSED, DOUBLE_SPECTRA, NULL, DOUBLE_ARRAY}

    Type type;
    String name;
    String source;
    String priorMatchSource;

    Field(String name, Type type, String source) {
        this(name, type, source, null);
    }

    Field(String name, Type type, String source, String priorMatchSource) {
        this.name = name;
        this.type = type;
        this.source = source;
        this.priorMatchSource = priorMatchSource;
    }

    Object getObject() {
        switch(type) {
            case DOUBLE:
            case DOUBLE_TRANSPOSED:
                return new ArrayList<Double>();
            case CELL:
                return new ArrayList<String>();
            case DOUBLE_SPECTRA:
                String[] types = source.split(",");
                ArrayList<ArrayList<double[]>> spectralMap = new ArrayList<ArrayList<double[]>>();
                for (String type : types) {
                    spectralMap.add(new ArrayList<double[]>());
                }
                return spectralMap;
            case NULL:
                return null;
            case DOUBLE_ARRAY:
                return new ArrayList<double[]>();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    void putValue(Object out, DataLine dl,  Waves waves, DataLine priorDataLine) {
        if (source == null || type == Type.NULL) {  //we skip this
            return;
        }
        switch (type) {
            case DOUBLE:
            case DOUBLE_TRANSPOSED:
                //check to see if we need to add and if so fall through to add
                if (priorDataLine != null && priorMatchSource != null) {
                    String priorValue = priorDataLine.hash.get(priorMatchSource);
                    if (priorValue.equals(dl.hash.get(priorMatchSource))) {
                        //nothing to do we already have this one
                        return;
                    }
                }
                ((ArrayList<Double>) out).add(Double.parseDouble(dl.hash.get(source)));
                return;
            case CELL:
                ((ArrayList<String>) out).add(dl.hash.get(source));
                return;
            case DOUBLE_SPECTRA:
                //these are the spectral
                String[] types = source.split(",");
                ArrayList<ArrayList<double[]>> spectralMap = (ArrayList<ArrayList<double[]>>) out;
                ArrayList<double[]> spectra;
                for (int i = 0; i < types.length; i++) {
                    spectralMap.get(i).add(dl.spectra.get(types[i]));
                }
                return;
            case DOUBLE_ARRAY:
                ArrayList<double[]> doubleArray = (ArrayList<double[]>) out;
                doubleArray.add(waves.values.get(dl.hash.get(source)));
                return;
            default:
                //unexpected
                throw new IllegalArgumentException("unexpected type " + type);
        }
    }

    @SuppressWarnings("unchecked")
    MLArray makeOutput(Object out) {
        if (source == null || type == Type.NULL) {  //we skip these
            return null;
        }
        switch (type) {
            case DOUBLE:
            case DOUBLE_TRANSPOSED:
                ArrayList<Double> aDouble = (ArrayList<Double>) out;
                int rows = type == Type.DOUBLE ? aDouble.size() : 1;
                return new MLDouble(name, arraylistDoubleToDoubleArray(aDouble), rows);
            case CELL:
                ArrayList<String> aCell = (ArrayList<String>) out;
                MLCell mlCell = new MLCell(name, new int[]{aCell.size(), 1});
                for (int i = 0; i < aCell.size(); i++) {
                    MLChar mlChar = new MLChar("", aCell.get(i));
                    mlCell.set(mlChar, i);
                }
                return mlCell;
            case DOUBLE_SPECTRA:
                //these are the spectral
                String[] types = source.split(",");
                ArrayList<ArrayList<double[]>> spectralMap = (ArrayList<ArrayList<double[]>>) out;
                MLCell spectraCells = new MLCell(name, new int[]{1, 4});
                for (int i = 0; i < types.length; i++) {
                    ArrayList<double[]> spectra = spectralMap.get(i);
                    double[][] spectralMatrix = new double[spectra.size()][];
                    for (int j = 0; j < spectra.size(); j++) {
                        spectralMatrix[j] = spectra.get(j);
                    }
                    spectraCells.set(new MLDouble(name, spectralMatrix), i);
                }
                return spectraCells;
            case DOUBLE_ARRAY:
                ArrayList<double[]> doubleArray = (ArrayList<double[]>) out;
                double[][] doubleMatrix = new double[doubleArray.size()][];
                for (int i = 0; i < doubleArray.size(); i++) {
                    doubleMatrix[i] = doubleArray.get(i);
                }
                return new MLDouble(name, doubleMatrix);
            default:
                //unexpected
                throw new IllegalArgumentException("unexpected type " + type);
        }
    }

    private double[] arraylistDoubleToDoubleArray(ArrayList<Double> list) {
        double[] doubles = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            doubles[i] = list.get(i);
        }
        return doubles;
    }
}


