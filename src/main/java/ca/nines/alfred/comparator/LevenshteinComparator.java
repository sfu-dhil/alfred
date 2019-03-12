package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class LevenshteinComparator extends Comparator {

    public static final double THRESHOLD = 0.6;

    public LevenshteinComparator(TextCollection collection, String stopWordsFile) {
        super(collection, stopWordsFile);
    }

    @Override
    public String getType() {
        return "lev";
    }

    @Override
    public double compare(String aId, String bId) {
        String aContent = collection.get(aId);
        String bContent = collection.get(bId);
        return levenshtein(aContent, bContent);
    }

    public double levenshtein(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }
        int maxLength = Math.max(a.length(), b.length());
        int limit = (int) Math.ceil(maxLength * (1.0 - THRESHOLD));
        if(Math.abs(a.length() - b.length()) > limit) {
            return 0;
        }
        LevenshteinDistance ld = new LevenshteinDistance(limit);
        int distance = ld.apply(a, b);
        if (distance <= 0) {
            return 0;
        }
        double similarity = 1.0 - (distance / ((double) maxLength));
        return similarity;
    }
}
