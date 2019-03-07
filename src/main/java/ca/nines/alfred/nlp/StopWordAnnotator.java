package ca.nines.alfred.nlp;


import ca.nines.alfred.cmd.StopWords;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StopWordAnnotator implements Annotator, CoreAnnotation<Boolean> {

    public static final String ANNOTATOR_CLASS = "stopword";

    private final Set<String> stopwords;

    private final Logger logger;

    public StopWordAnnotator(String name, Properties props) throws IOException {
        stopwords = new HashSet<>(50);
        logger = LoggerFactory.getLogger(this.getClass());
        String wordList = props.getProperty("stopword.wordlist");
        if(wordList == null || wordList.isEmpty()) {
            return;
        }
        InputStream in = this.getClass().getResourceAsStream("/" + StopWords.PATH + "/" + wordList);
        for(String line : IOUtils.readLines(in, StandardCharsets.UTF_8)) {
            if(line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            stopwords.add(line.toLowerCase().trim());
        }
        logger.info("Added " + stopwords.size() + " stop words.");
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
            if(stopwords.contains(token.word().toLowerCase())) {
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
                CoreAnnotations.TokensAnnotation.class
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
