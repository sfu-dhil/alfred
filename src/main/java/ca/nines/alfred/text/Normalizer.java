/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.text;

/**
 *
 * @author mjoyce
 */
public class Normalizer {

        public static String normalize(String text) {
        if(text == null) {
            return "";
        }
        return java.text.Normalizer
                .normalize(text, java.text.Normalizer.Form.NFKD)
                .toLowerCase()
                .replaceAll("(\\p{Graph})\\p{Punct}+(?=\\p{Graph})", "$1")
                .replaceAll("\\p{Punct}+", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 -]", "")
                .trim();
    }

}
