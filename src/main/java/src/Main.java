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
            String directoryPathExtReg    = "C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\regr\\";
            String filePath               = "C:\\Users\\UTENTE\\Documents\\NetBeansProjects\\JD_JSON_processor\\src\\resources\\specifications_new_full_FAT.txt"; 
            List<Map<String, Object>> jsonData = JsonReader.readJsonFile(filePath);
            
            
            // Esegui ulteriori operazioni sulla mappa tsDataMap
            //for (Map.Entry<String, TsData> entry : tsDataMap.entrySet()) {
            //    System.out.println("Serie: " + entry.getKey());
            //    System.out.println(entry.getValue());
            //}

        
            // Print or use the data as you prefer
            for (Map<String, Object> data : jsonData) {
                System.out.println("Series Name: " + data.get("series_name"));
                
                // Deserialization of JSON
                ObjectMapper mapper = new ObjectMapper();
                //ignore not predefined keys
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                //put also single values in arrays where it is specified
                mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                //set empty strings to null
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                //create the object
                DestSpecificationsModel model=mapper.readValue(mapper.writeValueAsString(data), DestSpecificationsModel.class);
                TSmodelSetup tsModelSetup = new TSmodelSetup(model, directoryPathExtReg);
                TramoSeatsSpecification TRAMOSEATSspec = tsModelSetup.getTsSpec();
                
                
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
