/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.entity;

import ca.nines.alfred.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author michael
 */
public class Paragraph implements Iterable<Sentence>, NGrams, Text {

    private String id;

    private String text;

    private String normalizedText;

    private List<Sentence> sentences;

    private Map<Paragraph, Double> similarParagraphs;

    public Paragraph() {
        sentences = new ArrayList<>(10);
        similarParagraphs = new HashMap<>();
    }

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
        if(text == null || text.equals("")) {
            StringBuilder sb = new StringBuilder();
            for(Sentence sentence : sentences) {
                sb.append(sentence.getText()).append("\n\n");
            }
            text = sb.toString();
        }
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
            normalizedText = Normalizer.normalize(getText());
        }
        return normalizedText;
    }

    /**
     * @return the sentences
     */
    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * @param sentences the sentences to set
     */
    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public void addSentence(Sentence sentence) {
        this.sentences.add(sentence);
    }

    @Override
    public Iterator<Sentence> iterator() {
        return sentences.iterator();
    }

    @Override
    public List<String> ngrams(int length) {
        List<String> ngrams = new ArrayList<>(10);
        for(Sentence s : sentences) {
            ngrams.addAll(s.ngrams(length));
        }
        return ngrams;
    }

    /**
     * @return the similarParagraphs
     */
    public Map<Paragraph, Double> getSimilarParagraphs() {
        return similarParagraphs;
    }

    /**
     * @param similarParagraphs the similarParagraphs to set
     */
    public void setSimilarParagraphs(Map<Paragraph, Double> similarParagraphs) {
        this.similarParagraphs = similarParagraphs;
    }

    public void addSimilarPararaph(Paragraph paragraph, double similarity) {
        similarParagraphs.put(paragraph, similarity);
    }

}
