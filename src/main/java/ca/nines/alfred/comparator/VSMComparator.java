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

package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.util.Tokenizer;
import ca.nines.alfred.vsm.VectorSpaceModel;

import java.io.IOException;

/**
 * Uses a vector space model to index the collection and generate TF-IDF statistics, then
 * compares documents using those statistics.
 *
 * {@link VectorSpaceModel}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Vector_space_model">Vector Space Model on Wikipedia</a>
 * @see <a href="https://en.wikipedia.org/wiki/Tf%E2%80%93idf">TF-IDF on Wikipedia</a>
 */
public class VSMComparator extends Comparator {

    private final VectorSpaceModel vsm;

    /**
     * Build the comparator.
     *
     * @param collection collection of text documents and their IDs
     * @param stopWordsFile optional name of a stopword file
     * @throws IOException if the stopword file isn't readable
     */
    public VSMComparator(TextCollection collection, String stopWordsFile) throws IOException {
        super(collection, stopWordsFile);
        Tokenizer tokenizer = new Tokenizer(stopWordsFile);
        vsm = new VectorSpaceModel(tokenizer);
        for(String id : collection.keys()) {
            vsm.add(id, collection.get(id));
        }
        vsm.computeWeights();
    }

    /**
     * If the VSM is already built there's no  need to recompute it.
     *
     * @param vsm the vector space model
     */
    public VSMComparator(VectorSpaceModel vsm) {
        super(null, null);
        this.vsm = vsm;
    }

    @Override
    public String getType() {
        return "vsm";
    }

    @Override
    public double compare(String aId, String bId) {
        double similarity = vsm.compare(aId, bId);
        if(similarity >= settings.getDouble("vsm_threshold")) {
            return Math.min(1.0, similarity);
        } else {
            return 0.0;
        }
    }
}
