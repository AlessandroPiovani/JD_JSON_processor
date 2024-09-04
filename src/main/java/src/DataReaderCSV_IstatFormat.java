package src;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataReaderCSV_IstatFormat {

    public static Map<String, TsData> readData(String filePath) throws IOException, ParseException {
        // Leggi il file CSV
        List<String[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(";");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].replace("\"", ""); // Rimuovi le virgolette
            }
            data.add(values);
        }
        br.close();

        // Converti le date nel formato "ANNOqMESE" in un formato Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> timestamps = new ArrayList<>();
        Pattern datePattern = Pattern.compile("([0-9]+)q([0-9]+)");
        for (int i = 1; i < data.size(); i++) { // Salta l'intestazione
            Matcher matcher = datePattern.matcher(data.get(i)[0]);
            if (matcher.find()) {
                String year = matcher.group(1);
                String month = matcher.group(2);
                String dateString = year + "-" + month + "-01";
                timestamps.add(dateFormat.parse(dateString));
            }
        }

        // Rimuovi la colonna delle date e la riga dei nomi delle serie
        String[] seriesNames = data.get(0); // La prima riga contiene i nomi delle serie
        //System.arraycopy(seriesNames, 1, seriesNames, 0, seriesNames.length - 1); // Sposta i dati a sinistra
        String[] newSeriesNames = new String[seriesNames.length - 1]; // Crea un nuovo array più corto di una posizione rispetto all'originale
        System.arraycopy(seriesNames, 1, newSeriesNames, 0, newSeriesNames.length); // Copia gli elementi a partire dall'indice 1 dell'array originale al nuovo array
        seriesNames = newSeriesNames; // Assegna il nuovo array a seriesNames
        
        
        data.remove(0); // Rimuovi la riga dei nomi delle serie
        // In ogni riga, rimuovi il primo valore, cioè la data
          
        for (String[] row : data) {
            System.arraycopy(row, 1, row, 0, row.length - 1); // Sposta i dati a sinistra
        }

        // Determina la frequenza della serie temporale
        Date d1 = timestamps.get(0);
        Date d2 = timestamps.get(1);
        int monthDiff = Math.abs(Integer.parseInt(new SimpleDateFormat("MM").format(d1))
                - Integer.parseInt(new SimpleDateFormat("MM").format(d2)));
        TsFrequency freq = (monthDiff == 1) ? TsFrequency.Monthly : TsFrequency.Quarterly;

        // Crea la mappa per memorizzare <NomeSerie, TsData>
        Map<String, TsData> tsDataMap = new HashMap<>();

        for (int i = 0; i < seriesNames.length; i++) {
            List<Double> seriesValuesList = new ArrayList<>();
            TsPeriod startPeriod = null;
            //System.out.println("\n" + seriesNames[i]);

            
            for (int j = 0; j < timestamps.size(); j++) {
                String value = data.get(j)[i];
                //System.out.print(j+") ");
                //System.out.print(value+"\n");
                
                if (!value.equals("NA")) {
                    if (startPeriod == null) {
                        // Trova la data di inizio per la serie, corrispondente al primo valore non NA
                        startPeriod = new TsPeriod(freq, Integer.parseInt(new SimpleDateFormat("yyyy").format(timestamps.get(j))),
                                timestamps.get(j).getMonth());
                    }
                    // Sostituisci la virgola con il punto per i decimali
                    seriesValuesList.add(Double.parseDouble(value.replace(",", ".")));
                }
            }

            if (startPeriod != null) {
                double[] seriesValues = seriesValuesList.stream().mapToDouble(Double::doubleValue).toArray();
                tsDataMap.put(seriesNames[i], new TsData(startPeriod, seriesValues, false));
            }
        }

        return tsDataMap;
    }
    
}