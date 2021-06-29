package ca.nines.alfred.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LanguageDecoder {

    private static Map<String,String> iso3 = new HashMap<>();

    private static Map<String,String> iso2 = new HashMap<>();

    private static void read() throws IOException {
        InputStream in = LanguageDecoder.class.getResourceAsStream("/iso-639-3.csv");
        InputStreamReader reader = new InputStreamReader(in);
        CSVParser csv = new CSVParser(reader, CSVFormat.DEFAULT, 0, 1);
        for(CSVRecord record : csv) {
            iso3.put(record.get(0), record.get(6));
            if(record.isSet(3)) {
                iso2.put(record.get(3), record.get(6));
            }
        }
    }

    public static String codeToLanguage(String code) {
        if(iso3.size() == 0) {
            try {
                read();
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(LanguageDecoder.class);
                logger.error("Cannot read language data: " + e.getMessage());
                return null;
            }
        }
        if(code == null) {
            return null;
        }
        if (code.length() == 2) {
            return iso2.get(code);
        }
        if (code.length() == 3) {
            return iso3.get(code);
        }
        Logger logger = LoggerFactory.getLogger(LanguageDecoder.class);
        logger.error("Language codes must be 2 or three characters. Got " + code);
        return null;
    }
}
