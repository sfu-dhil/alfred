package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;

abstract public class Comparator {

    protected final Corpus corpus;

    protected final String stopWordsFile;

    public Comparator(Corpus corpus, String stopWordsFile) {
        this.corpus = corpus;
        this.stopWordsFile = stopWordsFile;
    }

    abstract public String getType();

    abstract public double compare(String aId, String bId);
}
