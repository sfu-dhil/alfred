/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.annotator;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.Pair;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael
 */
public class StopWordAnnotator implements Annotator, CoreAnnotation<Boolean> {

    public static final String ANNOTATOR_CLASS = "stopword";

    private final Set<String> stopwords;

    private final String path = "/stopwords/nltk.txt";

    private final Logger logger;

    public StopWordAnnotator() {
        stopwords = new HashSet<>(50);
        logger = LoggerFactory.getLogger(this.getClass());
        InputStream in = this.getClass().getResourceAsStream(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ((line.length() == 0) || (line.charAt(0) == '#')) {
                    continue;
                }
                stopwords.add(line.toLowerCase());
            }
        } catch (Exception e) {
            logger.error("Cannot load stop word list resource.", e);
        }

    }

    public Set<String> getStopwords() {
        return Collections.unmodifiableSet(stopwords);
    }

    @Override
    public void annotate(Annotation annotation) {
        if(stopwords.isEmpty() ||  ! annotation.containsKey(CoreAnnotations.TokensAnnotation.class)) {
            return;
        }
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            if(stopwords.contains(token.word().toLowerCase()) || stopwords.contains(token.lemma().toLowerCase())) {
                token.set(StopWordAnnotator.class, true);
            } else {
                token.set(StopWordAnnotator.class, false);
            }
        }
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requires() {
        return Collections.unmodifiableSet(new ArraySet<>(Arrays.asList(
                CoreAnnotations.TextAnnotation.class,
                CoreAnnotations.TokensAnnotation.class,
                CoreAnnotations.LemmaAnnotation.class,
                CoreAnnotations.SentencesAnnotation.class,
                CoreAnnotations.PartOfSpeechAnnotation.class
        )));
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
        return Collections.singleton(CoreAnnotations.LemmaAnnotation.class);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.TYPE;
    }

}
