package src;

/**
 *
 * @author alessandro.piovani@istat.it
 */

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtRegDataReaderTSplus {

    public static Map<String, TsData> readTsFile(String directoryPath, String fileName, TsFrequency frequency, int startYear, int startPositionInYear) throws IOException {
        // Crea il percorso completo del file
        File file = new File(directoryPath, fileName);
        String fileNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");

        Map<String, TsData> tsDataMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<List<Double>> columns = new ArrayList<>();

            // Leggi ogni riga e processa i valori
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");

                // Inizializza le colonne al primo passaggio
                if (columns.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        columns.add(new ArrayList<>());
                    }
                }

                // Aggiungi i valori alle rispettive colonne
                for (int i = 0; i < values.length; i++) {
                    columns.get(i).add(Double.parseDouble(values[i]));
                }
            }

            // Crea un oggetto TsData per ogni colonna e aggiungilo alla mappa
            for (int i = 0; i < columns.size(); i++) {
                String variableName = fileNameWithoutExtension + "_" + (i + 1);
                double[] valuesArray = columns.get(i).stream().mapToDouble(Double::doubleValue).toArray();
                TsPeriod firstPeriod = new TsPeriod(frequency, startYear, startPositionInYear);
                TsData tsData = new TsData(firstPeriod, valuesArray, true);
                tsDataMap.put(variableName, tsData);
            }
        }

        return tsDataMap;
    }

//    public static void main(String[] args) {
//        try {
//            String directoryPath = "C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\regr";
//            //String fileName = "bb05ipi.txt";
//            String fileName = "lym_02.txt";
//            TsFrequency frequency = TsFrequency.Monthly; // Frequenza dei dati
//            int startYear = 1990; // Anno di inizio
//            int startPositionInYear = 0; // Posizione di inizio nell'anno
//
//            Map<String, TsData> tsDataMap = readTsFile(directoryPath, fileName, frequency, startYear, startPositionInYear);
//
//            // Stampa i risultati
//            tsDataMap.forEach((key, value) -> System.out.println(key + " -> " + value));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
