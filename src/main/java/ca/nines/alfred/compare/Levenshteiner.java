/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.compare;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 *
 * @author mjoyce
 */
public class Levenshteiner {

    public static final double THRESHOLD = 0.6;

    public static double compare(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }

        int maxLength = Math.max(a.length(), b.length());
        int limit = (int) Math.ceil(maxLength * (1.0 - THRESHOLD));
        LevenshteinDistance ld = new LevenshteinDistance(limit);
        int distance = ld.apply(a, b);
        if (distance <= 0) {
            return 0;
        }
        double similarity = 1.0 - (distance / ((double) maxLength));
        return similarity;
    }

}
