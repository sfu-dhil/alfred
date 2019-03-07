/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.util;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;

public class Text {

    public static final double LEVENSHTEIN_THRESHOLD = 0.6;

    public static final double COSINE_THRESHOLD = 0.9;

    public static double levenshtein(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }
        int maxLength = Math.max(a.length(), b.length());
        int limit = (int) Math.ceil(maxLength * (1.0 - LEVENSHTEIN_THRESHOLD));
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

    public static double cosine(String a, String b) {
        CosineDistance cd = new CosineDistance();
        double distance = cd.apply(a, b);
        double similarity = 1.0 - distance;
        if(similarity < COSINE_THRESHOLD) {
            return 0;
        }
        return similarity;
    }

    public static String normalize(String text) {
        if(text == null) {
            return "";
        }
        return Normalizer
                .normalize(text, Normalizer.Form.NFKD)
                .toLowerCase()
                .replaceAll("(\\p{Graph})\\p{Punct}+(?=\\p{Graph})", "$1")
                .replaceAll("\\p{Punct}+", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 -]", "")
                .trim();
    }

}
