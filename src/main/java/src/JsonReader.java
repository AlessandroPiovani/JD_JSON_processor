package src;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 *
 * @author alessandro.piovani@istat.it
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonReader {

    public static List<Map<String, Object>> readJsonFile(String filePath) throws IOException {
        // Crea un ObjectMapper, che è la classe principale di Jackson per il mapping JSON<->Java
        ObjectMapper objectMapper = new ObjectMapper();

        // Legge il file JSON e lo converte in una lista di mappe
        List<Map<String, Object>> jsonData = objectMapper.readValue(
                new File(filePath), new TypeReference<List<Map<String, Object>>>() {});

        return jsonData;
    }
}

