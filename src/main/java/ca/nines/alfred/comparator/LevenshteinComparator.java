package ca.nines.alfred.comparator;

import ca.nines.alfred.util.Settings;
import ca.nines.alfred.util.Text;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LevenshteinComparator implements Comparator  {

    final Map<String, String> text;

    final Logger logger;

    final double threshold;

    final int minLength;

    public LevenshteinComparator() {
        logger = LoggerFactory.getLogger(getClass());
        text = new HashMap<>();
        threshold = Settings.getInstance().getDouble("lev_threshold");
        minLength = Settings.getInstance().getInt("min_length");
    }

    public void add(String id, String content) {
        if(content.length() < minLength) {
            return;
        }
        text.put(id, Text.normalize(content));
    }

    public void complete() {
        logger.info("LEV contains {} documents. Match threshold is {}.", text.size(), threshold);
    }

    public double compare(String srcId, String dstId) {
        String a = text.get(srcId);
        String b = text.get(dstId);

        if(a == null || b == null) {
            return 0;
        }

        if(a.equals(b)) {
            return 1.0;
        }

        int maxLength = Math.max(a.length(), b.length());
        int limit = (int) Math.ceil(maxLength * (1.0 - threshold));
        if(Math.abs(a.length() - b.length()) > limit) {
            return 0;
        }
        LevenshteinDistance ld = new LevenshteinDistance(limit);
        int distance = ld.apply(a, b);
        if (distance <= 0) {
            return 0;
        }
        return 1.0 - (distance / ((double) maxLength));
    }

}
