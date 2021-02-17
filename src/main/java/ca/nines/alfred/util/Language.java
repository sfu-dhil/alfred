package ca.nines.alfred.util;

public class Language {

    public static String codeToLanguage(String code) {
        switch (code) {
            case "de":
                return "German";
            case "en":
                return "English";
            case "fr":
                return "French";
            case "it":
                return "Italian";
            case "es":
                return "Spanish";
            default:
                return "unknown";
        }
    }

}
