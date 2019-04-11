package ca.nines.alfred.comparator;

import ca.nines.alfred.tokenizer.Tokenizer;
import ca.nines.alfred.util.Settings;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CosineComparator implements Comparator {

    final Map<String, Map<String, Integer>> termCount;

    final Tokenizer tokenizer;

    final Logger logger;

    final int minLength;

    final double threshold;

    public CosineComparator(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        termCount = new HashMap<>();
        minLength = Settings.getInstance().getInt("min_length");
        threshold = Settings.getInstance().getDouble("cos_threshold");
        logger = LoggerFactory.getLogger(getClass());
    }

    public void add(String id, String content) {
        if(content.length() < minLength) {
            return;
        }

        if(termCount.containsKey(id)) {
            logger.error("ID {} has already been added to the comparator. Skipping.", id);
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        for(String token : tokenizer.tokenize(content)) {
            if( ! counts.containsKey(token)) {
                counts.put(token, 0);
            }
            counts.put(token, counts.get(token)+1);
        }

        termCount.put(id, counts);
    }

    public void complete() {
        // do nothing.
    }

    public double compare(String srcId, String dstId) {
        Map<String, Integer> srcCounts = termCount.get(srcId);
        Map<String, Integer> dstCounts = termCount.get(dstId);

        if(srcCounts == null || dstCounts == null) {
            return 0;
        }

        Set<String> common = SetUtils.intersection(srcCounts.keySet(), dstCounts.keySet());

        double numerator = 0;
        for(String term : common) {
            numerator += srcCounts.get(term) * dstCounts.get(term);
        }

        double srcDenominator = 0;
        for(String term : srcCounts.keySet()) {
            srcDenominator += srcCounts.get(term) * srcCounts.get(term);

        }

        double dstDenominator = 0;
        for(String term : dstCounts.keySet()) {
            dstDenominator += dstCounts.get(term) * dstCounts.get(term);
        }

        double similarity = numerator / (Math.sqrt(srcDenominator) * Math.sqrt(dstDenominator));
        if(similarity < threshold) {
            return 0;
        }
        return similarity;
    }

}
