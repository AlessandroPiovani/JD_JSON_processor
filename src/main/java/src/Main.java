package src;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alessandro.piovani@istat.it
 */
public class Main {

        public static void main(String[] args) {
        try {

            
            DataReaderCSV_IstatFormat reader = new DataReaderCSV_IstatFormat();
            Map<String, TsData> tsDataMap = reader.readData("C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\grezziFAT.csv");
            String directoryPathExtReg = "C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\regr\\";
            
            // Esegui ulteriori operazioni sulla mappa tsDataMap
            //for (Map.Entry<String, TsData> entry : tsDataMap.entrySet()) {
            //    System.out.println("Serie: " + entry.getKey());
            //    System.out.println(entry.getValue());
            //}

        
            String filePath = "C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\specifications_new_full_FAT.txt"; 
            List<Map<String, Object>> jsonData = JsonReader.readJsonFile(filePath);
            
            // Stampa o utilizza i dati come preferisci 
            for (Map<String, Object> data : jsonData) {
                System.out.println("Series Name: " + data.get("series_name"));
                // Puoi accedere agli altri campi della mappa come preferisci
                               
                // Da qui in poi DUBBI
                
                //DestSpecificationsModel destSpecModel = new DestSpecificationsModel("TS",data.toString());
                //TSmodelSetup tsModelSetup = new TSmodelSetup(destSpecModel);
                
                //deserializziamo per prendere i campi custom o che non sono direttamente
                //trattati da jDemetra in base al json
                ObjectMapper mapper = new ObjectMapper();
                //ignora le chiavi non definite (sono quelle trattate direttamente)
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                //accetta singolo valore dove prevista anche array
                mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                //imposta a null le stringhe vuote
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                //crea l'oggetto corrispondente 
                DestSpecificationsModel model=mapper.readValue(mapper.writeValueAsString(data), DestSpecificationsModel.class);
                TSmodelSetup tsModelSetup = new TSmodelSetup(model, directoryPathExtReg);
                TramoSeatsSpecification TRAMOSEATSspec = tsModelSetup.getTsSpec();
                
                //for debug
//                if(data.get("series_name").equals("DIVID33"))
//                {
//                           System.out.println("\n\n_____________________SERIE___________________\n\n");
//                           System.out.println(tsDataMap.get("DIVID33"));
//                           System.out.println("\n\n_____________________JSON___________________\n\n");
//                           System.out.println();
//                           CompositeResults rslt2 = TramoSeatsProcessingFactory.process(tsDataMap.get("DIVID33"), TRAMOSEATSspec );
//                           TsData sa_data2 = rslt2.getData("sa", TsData.class);
//                           System.out.println(sa_data2);
//
//                }
                //end for debug
                
                //ProcessingContext context = new ProcessingContext();
                
//                if(data.get("series_name").equals("C_DEFL"))
//                {
//                    System.out.println("debug");
//                }    
                
                ProcessingContext context = tsModelSetup.getContext();
                CompositeResults rslt = TramoSeatsProcessingFactory.process(tsDataMap.get(data.get("series_name")), TRAMOSEATSspec, context);
                TsData sa_data = rslt.getData("sa", TsData.class);
                System.out.println(sa_data);
                
            }
                  
        } 
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
    }
    
}
