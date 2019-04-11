package ca.nines.alfred.comparator;

import ca.nines.alfred.tokenizer.Tokenizer;
import ca.nines.alfred.util.Settings;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VsmComparator implements Comparator {

    final Map<String, Map<String, Double>> model;

    final Map<String, Integer> docTermCounts;

    final Tokenizer tokenizer;

    final int minLength;

    final Logger logger;

    final double threshold;

    boolean complete;

    public VsmComparator(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        model = new HashMap<>();
        docTermCounts = new HashMap<>();
        minLength = Settings.getInstance().getInt("min_length");
        threshold = Settings.getInstance().getDouble("vsm_threshold");
        logger = LoggerFactory.getLogger(getClass());
        complete = false;
    }

    public void add(String id, String content) {
        if(complete) {
            logger.error("Cannot add {} after completing the model.");
        }
        if (content.length() < minLength) {
            return;
        }

        Map<String, Double> w = new HashMap<>();

        for (String term : tokenizer.tokenize(content)) {
            if (!w.containsKey(term)) {
                w.put(term, 0.0);
                if (!docTermCounts.containsKey(term)) {
                    docTermCounts.put(term, 0);
                }
                docTermCounts.put(term, docTermCounts.get(term) + 1);
            }
            w.put(term, w.get(term) + 1);
        }
        model.put(id, w);
   }

    public void complete() {
        logger.info("VSM contains {} documents. Match threshold is {}.", model.size(), threshold);
        logger.info("VSM using {} tokenizer.", tokenizer.getClass().getSimpleName());
        for(String id : model.keySet()) {
            Map<String, Double> w = model.get(id);
            if(w.size() == 0) {
                continue;
            }
            for(String term : w.keySet()) {
                double idf = Math.log10(model.size() / (double)docTermCounts.get(term));
                double tf = w.get(term);
                double weight = tf * idf;
                w.put(term, weight);
            }
        }

        complete = true;
    }

    public double compare(String srcId, String dstId) {
        if( ! complete) {
            throw new RuntimeException("Cannot compare documents until model is complete.");
        }
        Map<String, Double> srcWeights = model.get(srcId);
        Map<String, Double> dstWeights = model.get(dstId);

        if(srcWeights == null || dstWeights == null) {
            return 0;
        }

        double value = 0.0;
        for(String term : SetUtils.intersection(srcWeights.keySet(), dstWeights.keySet())) {
            value += srcWeights.get(term) * dstWeights.get(term);
        }

        double srcNorm = 0;
        for(double w : srcWeights.values()) {
            srcNorm += w * w;
        }

        double dstNorm = 0;
        for(double w : dstWeights.values()) {
            dstNorm += w * w;
        }

        double similarity = value / (Math.sqrt(srcNorm) * Math.sqrt(dstNorm));
        if(similarity < threshold) {
            return 0;
        }
        return similarity;
    }

}
