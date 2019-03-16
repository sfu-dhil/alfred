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
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * Uses Levenshtein distance to estimate the similarity between two documents. This method is very slow,
 * but also precise and sensitive to word order.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein Distance</a>
 */
public class LevenshteinComparator extends Comparator {

    public LevenshteinComparator(TextCollection collection, String stopWordsFile) {
        super(collection, stopWordsFile);
    }

    @Override
    public String getType() {
        return "lev";
    }

    @Override
    public double compare(String aId, String bId) {
        String aContent = collection.get(aId);
        String bContent = collection.get(bId);
        return levenshtein(aContent, bContent);
    }

    /**
     * Do the Levenshtein calculation.
     *
     * @param a first text to compare
     * @param b second text to compare
     * @return a percentage match between the two documents.
     */
    double levenshtein(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }
        int maxLength = Math.max(a.length(), b.length());
        int limit = (int) Math.ceil(maxLength * (1.0 - settings.getDouble("levenshtein_threshold")));
        if(Math.abs(a.length() - b.length()) > limit) {
            return 0;
        }
        LevenshteinDistance ld = new LevenshteinDistance(limit);
        int distance = ld.apply(a, b);
        if (distance <= 0) {
            return 0;
        }
        double similarity = 1.0 - (distance / ((double) maxLength));
        return similarity;
    }
}
