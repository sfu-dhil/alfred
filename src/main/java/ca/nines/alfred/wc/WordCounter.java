package ca.nines.alfred.wc;

abstract public class WordCounter {

    protected final boolean hasStopWords;

    public WordCounter() {
        hasStopWords = false;
    }

    public WordCounter(String stopWordFile) {
        if(stopWordFile == null) {
            hasStopWords = false;
        } else {
            hasStopWords = true;
        }
    }

    abstract public long count(String text);

}
