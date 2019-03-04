/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.nlp;

import ca.nines.alfred.annotator.PunctuationAnnotator;
import ca.nines.alfred.annotator.StemAnnotator;
import ca.nines.alfred.annotator.StopWordAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;

/**
 *
 * @author michael
 */
public class CoreNLPBuilder {

    private static StanfordCoreNLP pipeline;

    public static StanfordCoreNLP getInstance() {
        if (pipeline == null) {
            Properties properties = new Properties();
            properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,stopword,stem,punct");
            properties.setProperty("customAnnotatorClass.stopword", StopWordAnnotator.class.getCanonicalName());
            properties.setProperty("customAnnotatorClass.stem", StemAnnotator.class.getCanonicalName());
            properties.setProperty("customAnnotatorClass.punct", PunctuationAnnotator.class.getCanonicalName());
            pipeline = new StanfordCoreNLP(properties);
        }
        return pipeline;
    }

}
