package ca.nines.alfred.vsm;

import ca.nines.alfred.util.Tokenizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorSpaceModel {

    private Map<String, Map<String, Integer>> vsm;

    private Map<String, Integer> termCount;

    private Tokenizer tokenizer;

    public VectorSpaceModel(Tokenizer tokenizer) {
        vsm = new HashMap<>();
        termCount = new HashMap<>();
        this.tokenizer = tokenizer;
    }

    public void add(String id, String content) {
        List<String> terms = tokenizer.words(content);

        Map<String, Integer> d = new HashMap<>();
        for(String term : terms) {
            if( ! d.containsKey(term)) {
                d.put(term, 1);
            } else {
                d.put(term, d.get(term) + 1);
            }
        }
        vsm.put(id, d);

        for(String term : d.keySet()) {
            int c = d.get(term);
            if( ! termCount.containsKey(term)) {
                termCount.put(term, d.get(term));
            } else {
                termCount.put(term, termCount.get(term) + d.get(term));
            }
        }
    }

    protected double weight(String id, String term) {
        return augmentedFrequency(id, term) * idf(term);
    }

    protected int frequency(String id, String term) {
        if( ! vsm.containsKey(id)) {
            return 0;
        }
        Map<String, Integer> d = vsm.get(id);
        if( ! d.containsKey(term)) {
            return 0;
        }
        return d.get(term);
    }

    protected double augmentedFrequency(String id, String term) {
        int f = frequency(id, term);

        if(f == 0) {
            return 0;
        }

        int max = Collections.max(vsm.get(id).values());
        return 0.5 + 0.5 * f / max;
    }

    // this should only be calculated once per term.
    protected double idf(String term) {
        int d = 0;
        for(String id : vsm.keySet()) {
            if(vsm.get(id).containsKey(term)) {
                d++;
            }
        }
        return Math.log10(vsm.size() / (double)d);
    }




}
