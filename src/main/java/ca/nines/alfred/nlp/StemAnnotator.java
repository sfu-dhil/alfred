/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.nlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.process.Stemmer;
import edu.stanford.nlp.process.StemmerBuilder;
import edu.stanford.nlp.util.ArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author michael
 */
public class StemAnnotator implements Annotator, CoreAnnotation<String> {

    private final Stemmer stemmer;

    private final Logger logger;

    public StemAnnotator() {
        stemmer = StemmerBuilder.instance();
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void annotate(Annotation annotation) {
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.word().toLowerCase();
            String stem = stemmer.stem(word);
            token.set(StemAnnotator.class, stem);
        }
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requires() {
        return Collections.unmodifiableSet(new ArraySet<>(Arrays.asList(
                CoreAnnotations.TextAnnotation.class,
                CoreAnnotations.TokensAnnotation.class,
                CoreAnnotations.SentencesAnnotation.class
        )));
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
        return Collections.singleton(CoreAnnotations.TokensAnnotation.class);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
