package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class Comparator {

    public static final int MIN_LENGTH = 64;

    protected final Logger logger;

    protected final TextCollection collection;

    protected final String stopWordsFile;

    public Comparator(TextCollection collection, String stopWordsFile) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.collection = collection;
        this.stopWordsFile = stopWordsFile;
    }

    abstract public String getType();

    abstract public double compare(String aId, String bId);
}
