package ca.nines.alfred.vsm;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.util.Tokenizer;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class VectorSpaceModel {

    protected final Logger logger;

    private final Tokenizer tokenizer;

    private final Map<String, Integer> docTermCounts;

    private final Map<String, Map<String, Double>> model;

    public VectorSpaceModel(Tokenizer tokenizer) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.tokenizer = tokenizer;
        docTermCounts = new HashMap<>();
        model = new HashMap<>();
    }

    public void add(String id, String text) {
        List<String> terms = tokenizer.tokenize(text);
        Map<String, Double> w = new HashMap<>();

        for(String term : terms) {
            if(w.containsKey(term)) {
                w.put(term, w.get(term) + 1.0);
            } else {
                w.put(term, 1.0);
                if(docTermCounts.containsKey(term)) {
                    docTermCounts.put(term, docTermCounts.get(term) + 1);
                } else {
                    docTermCounts.put(term, 1);
                }
            }
        }
        model.put(id, w);
    }

    public void computeWeights() {
        for(String id : model.keySet()) {
            Map<String, Double> w = model.get(id);
            if(w.size() == 0) {
                continue;
            }
            double max = Collections.max(w.values());
            for(String term : w.keySet()) {
                double idf = Math.log10(model.size() / (double)docTermCounts.get(term));
                double atf = 0.5 + 0.5 * w.get(term) / max;
                double weight = atf * idf;
                w.put(term, weight);
            }
        }
    }

    public double compare(String srcId, String dstId) {
        Map<String, Double> srcWeights = model.get(srcId);
        Map<String, Double> dstWeights = model.get(dstId);

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

        return value / (Math.sqrt(srcNorm) * Math.sqrt(dstNorm));
    }

}
