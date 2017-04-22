package play.modules.s3blobs;

import org.apache.commons.lang.math.NumberUtils;

import play.Play;
import play.exceptions.ConfigurationException;

public class ConfigHelper {
	
    public static Boolean getBoolean(String configKey, Boolean defaultValue) {
        String asStr = Play.configuration.getProperty(configKey);
        if (asStr == null || asStr.length() == 0) {
            return defaultValue;
        }

        if (asStr.equals("true") || asStr.equals("false")) {
            return Boolean.parseBoolean(asStr);
        }

        throw new ConfigurationException(configKey + " must be either true or false");
    }
    
    public static int getInt(String configKey, int defaultValue) {
        String asStr = Play.configuration.getProperty(configKey);
        if (asStr == null || asStr.length() == 0 || !NumberUtils.isDigits(asStr)) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(asStr);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(configKey + " must a whole number");
        }
    }    

}
