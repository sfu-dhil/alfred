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
import org.apache.commons.text.similarity.CosineDistance;

import java.util.regex.Pattern;

/**
 * Uses a cosine distance algorithm to estimate the similarity between two documents.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Cosine Similarity on Wikipedia</a>
 */
public class CosineComparator extends Comparator {

    /**
     * The cosine algorithm requires words with letters to operate or it will throw an exception.
     */
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

    /**
     * Calculate the cosine distance and estimate the similarity from it.
     *
     * @param a first text to compare
     * @param b second text to compare
     * @return a percentage match of the two documents
     */
    double cosine(String a, String b) {
        if(a.length() == 0 || b.length() == 0) {
            return 0;
        }
        if( ! CHARS.matcher(a).find() || ! CHARS.matcher(b).find()) {
            return 0;
        }
        CosineDistance cd = new CosineDistance();
        double distance = cd.apply(a, b);
        double similarity = 1.0 - distance;
        if(similarity < settings.getDouble("cosine_threshold")) {
            return 0;
        }
        return similarity;
    }
}
