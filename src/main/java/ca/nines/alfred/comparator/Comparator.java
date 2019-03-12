package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;

abstract public class Comparator {

    protected final TextCollection collection;

    protected final String stopWordsFile;

    public Comparator(TextCollection collection, String stopWordsFile) {
        this.collection = collection;
        this.stopWordsFile = stopWordsFile;
    }

    abstract public String getType();

    abstract public double compare(String aId, String bId);
}
