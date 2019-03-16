package ca.nines.alfred.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public class Settings {

    private static Settings instance = null;

    final Logger logger;

    private Properties properties = null;

    public static Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
            instance.properties = new Properties();
            InputStream in = Settings.class.getResourceAsStream("/defaults.properties");
            try {
                instance.properties.load(in);
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private Settings(){
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public int getInt(String name) {
        if(properties.containsKey(name)) {
            return Integer.parseInt(properties.getProperty(name));
        } else {
            logger.error("Unknown setting name " + name);
        }
        return 0;
    }

    public double getDouble(String name) {
        if(properties.containsKey(name)) {
            return Double.parseDouble(properties.getProperty(name));
        } else {
            logger.error("Unknown setting name " + name);
        }
        return 0;
    }

    public String getString(String name) {
        if(properties.containsKey(name)) {
            return properties.getProperty(name);
        } else {
            logger.error("Unknown setting name " + name);
        }
        return null;
    }

    public String[] list() {
        Set<String> propertyNames = properties.stringPropertyNames();
        String[] names = propertyNames.toArray(new String[propertyNames.size()]);
        Arrays.sort(names);
        return names;
    }

    public void set(String name, String value) {
        properties.setProperty(name, value);
    }

}
