package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.util.Text;

public class LevenshteinComparator extends Comparator {

    public LevenshteinComparator(Corpus corpus) {
        super(corpus);
    }

    @Override
    public String getType() {
        return "lev";
    }

    @Override
    public double compare(Report a, Report b) {
        String aContent = a.getComparableContent();
        String bContent = b.getComparableContent();
        return Text.levenshtein(aContent, bContent);
    }
}
