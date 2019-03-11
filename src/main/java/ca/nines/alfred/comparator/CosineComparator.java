package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.util.Text;

public class CosineComparator extends Comparator {

    public CosineComparator(Corpus corpus, String stopWordsFile) {
        super(corpus, stopWordsFile);
    }

    @Override
    public String getType() {
        return "cos";
    }

    @Override
    public double compare(Report a, Report b) {
        String aContent = a.getComparableContent();
        String bContent = b.getComparableContent();
        return Text.cosine(aContent, bContent);
    }
}
