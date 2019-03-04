/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.entity;

import ca.nines.alfred.annotator.PunctuationAnnotator;
import ca.nines.alfred.annotator.StemAnnotator;
import ca.nines.alfred.annotator.StopWordAnnotator;
import ca.nines.alfred.text.Normalizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author michael
 */
public class Sentence implements Iterable<CoreLabel>, NGrams, Text {

    private String id;

    private String text;

    private String normalizedText;
    
    private CoreSentence data;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
        this.normalizedText = null;
    }

    @Override
    public String getNormalizedText() {
        if(normalizedText == null) {
            normalizedText = Normalizer.normalize(text);
        }
        return normalizedText;
    }

    /**
     * @return the data
     */
    public CoreSentence getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(CoreSentence data) {
        this.data = data;
    }

    @Override
    public Iterator<CoreLabel> iterator() {
        return data.tokens().iterator();
    }

    public List<String> ngrams(int length) {
        List<String> ngrams = new ArrayList<>(10);
        StringBuilder sb = new StringBuilder(10);
        for (CoreLabel token : data.tokens()) {
            if (token.get(StopWordAnnotator.class)) {
                continue;
            }
            if (!token.get(PunctuationAnnotator.class).isEmpty()) {
                continue;
            }
            sb.append(token.get(StemAnnotator.class));
        }
        String content = sb.toString().toLowerCase().replaceAll("\\W", "");

        if (content.length() < length) {
            return ngrams;
        }
        for (int i = 0; i < content.length() - length + 1; i++) {
            ngrams.add(content.substring(i, i + length));
        }

        return ngrams;
    }

}
