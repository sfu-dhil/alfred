package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.TextCollection;
import org.apache.commons.text.similarity.CosineDistance;
import org.slf4j.Logger;

import java.util.regex.Pattern;

public class CosineComparator extends Comparator {

    public static final double THRESHOLD = 0.9;

    private static final Pattern CHARS = Pattern.compile("[a-zA-Z]");

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

    public double cosine(String a, String b) {
        if(a.length() == 0 || b.length() == 0) {
            return 0;
        }
        if( ! CHARS.matcher(a).find() || ! CHARS.matcher(b).find()) {
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
