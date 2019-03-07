package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;

abstract public class Comparator {

    protected final Corpus corpus;

    public Comparator(Corpus corpus) {
        this.corpus = corpus;
    }

    abstract public String getType();

    abstract public double compare(Report a, Report b);
}
