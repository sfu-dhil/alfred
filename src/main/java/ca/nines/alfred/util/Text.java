/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.util;

import java.text.Normalizer;

public class Text {

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
