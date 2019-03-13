package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;
import org.apache.commons.text.similarity.CosineDistance;

public class CosineComparator extends Comparator {

    public static final double THRESHOLD = 0.9;

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
        CosineDistance cd = new CosineDistance();
        double distance = cd.apply(a, b);
        double similarity = 1.0 - distance;
        if(similarity < THRESHOLD) {
            return 0;
        }
        return similarity;
    }
}
