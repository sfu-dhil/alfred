/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.stanford.nlp.process;

/**
 *
 * @author michael
 */
public class StemmerBuilder {

    private static Stemmer stemmer;

    public static Stemmer instance() {
        if(stemmer == null) {
            stemmer = new Stemmer();
        }
        return stemmer;
    }

}
