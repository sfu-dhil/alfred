package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;
import org.apache.commons.text.similarity.CosineDistance;

public class CosineComparator extends Comparator {

    public static final double THRESHOLD = 0.9;

    public static final int MIN_LENGTH = 64;

    public CosineComparator(TextCollection collection, String stopWordsFile) {
        super(collection, stopWordsFile);
    }

    @Override
    public String getType() {
        return "cos";
    }

    @Override
    public double compare(String aId, String bId) {
        String aContent = collection.get(aId);
        String bContent = collection.get(bId);
        return cosine(aContent, bContent);
    }

    public static double cosine(String a, String b) {
        if(a.length() < MIN_LENGTH || b.length() < MIN_LENGTH) {
            return 0;
        }
        CosineDistance cd = new CosineDistance();
        double distance = cd.apply(a, b);
        double similarity = 1.0 - distance;
        if(similarity < THRESHOLD) {
            return 0;
        }
        return similarity;
    }
}
