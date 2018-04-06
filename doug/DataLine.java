package doug;

import java.util.HashMap;
import java.util.Map;

class DataLine {
    private static final String[] COLUMNS = {
            "id",
            "mrn",
            "core_id",
            "bin_index",
            "original_length",
            "fixation_length",
            "bin_cancer_percent",
            "gleason_score",
            "primary_grade",
            "secondary_grade",
            "pin",
            "asap",
            "console_serial_number",
            "handpiece_code",
            "spectrometer_id",
            "core_location",
            "core_cancer_percent",
            "core_gleason",
            "core_primary_grade",
            "core_secondary_grade",
            "core_pin",
            "core_asap",
            "procedure",
            "check_flag",
            "check_reason",
            "label",
            "wave_index"
    };

    private static final String[] SPECTRAL_TYPES = {
            "spectral280nm",
            "spectral340nm",
            "darkspectra",
            "spectrabroadband"
    };

    Map<String, String> hash;
    Map<String, double[]> spectra;

    DataLine(String line) {
        hash = new HashMap<>();
        spectra = new HashMap<String, double[]>();

        String[] data = line.split(",");

        for (int i = 0; i < COLUMNS.length; i++) {
            hash.put(COLUMNS[i], data[i]);
        }

        //then add in special values
        hash.put("site", hash.get("mrn").substring(0,2));
        //-1 not cancer; 1 cancer
        hash.put("core_label", hash.get("core_cancer_percent").matches("[1-9]") ? "1" : "-1");
        hash.put("bin_label", hash.get("label").matches("[1-9]") ? "1" : "-1");

        //now get wave data
        for (int i = 0; i < SPECTRAL_TYPES.length; i++) {
            double[] spectralData = new double[Waves.WAVES_PER_SPECTRUM];
            spectra.put(SPECTRAL_TYPES[i], spectralData);
            //make everything doubles
            for (int j = 0; j < Waves.WAVES_PER_SPECTRUM; j++) {
                spectralData[j] = Double.parseDouble(data[COLUMNS.length + i * Waves.WAVES_PER_SPECTRUM + j]);
            }
        }
    }

}
