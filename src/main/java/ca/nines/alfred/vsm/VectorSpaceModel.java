/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

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

    final Tokenizer tokenizer;

    final Map<String, Integer> docTermCounts;

    final Map<String, Map<String, Double>> model;

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
                double tf = w.get(term); // should this be augmented term frequencies? dunno.
                double weight = tf * idf;
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
