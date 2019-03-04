/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author michael
 */
public class NGramCounter {

    private final Map<String, Integer> counter;

    public NGramCounter() {
        counter = new HashMap<>(50);
    }

    public void add(List<String> ngrams) {
        for (String ngram : ngrams) {
            if (counter.containsKey(ngram)) {
                counter.put(ngram, counter.get(ngram) + 1);
            } else {
                counter.put(ngram, 1);
            }
        }
    }

    public void clear() {
        counter.clear();
    }

    public int count(String ngram) {
        if (counter.containsKey(ngram)) {
            return counter.get(ngram);
        }
        return 0;
    }

    public List<String> sort() {
        List<String> ngrams = new ArrayList<>(counter.keySet());
        // Defined Custom Comparator here
        Collections.sort(ngrams, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(counter.get(b), counter.get(a));
            }
        });
        return ngrams;
    }

    public List<String> leastFrequent(List<String> ngrams, int size) {
        List<String> tmp = new ArrayList<>(ngrams);
        Collections.sort(tmp, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(counter.get(a), counter.get(b));
            }
        });
        if(tmp.size() < size) {
            return tmp;
        }
        return tmp.subList(0, size);
    }

}
