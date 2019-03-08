package ca.nines.alfred.util;

public class TokenizerFactory {

    private static Tokenizer instance;

    private static String stopWordFile;

    public static Tokenizer getInstance() {
        if(instance == null) {
            instance = new Tokenizer(stopWordFile);
        }
        return instance;
    }

    public static void setStopWordFile(String stopWordFile) {
        if(instance != null) {
            throw new RuntimeException("Cannot reconfigure tokenizer.");
        }
        TokenizerFactory.stopWordFile = stopWordFile;
    }
}
