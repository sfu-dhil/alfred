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
 *
 * @author michael
 */
public class Document implements Iterable<Paragraph>, NGrams, Text {

    private String filename;

    private String title;

    private Map<String, String> metadata;

    private String id;

    private String text;

    private String normalizedText;

    private List<Paragraph> paragraphs;

    private Map<Document, Double> similarDocs;

    public Document() {
        paragraphs = new ArrayList<>(10);
        similarDocs = new HashMap<>();
        metadata = new HashMap<>();
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
    @Override
    public String getText() {
        if(text == null || text.equals("")) {
            StringBuilder sb = new StringBuilder();
            for(Paragraph paragraph : paragraphs) {
                sb.append(paragraph.getText()).append("\n\n");
            }
            text = sb.toString();
        }
        return text;
    }

    /**
     * @param text the text to set
     */
    @Override
    public void setText(String text) {
        this.text = text;
        this.normalizedText = null;
    }

    @Override
    public String getNormalizedText() {
        if(normalizedText == null || normalizedText.equals("")) {
            normalizedText = Normalizer.normalize(getText());
        }
        return normalizedText;
    }

    /**
     * @return the paragraphs
     */
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    /**
     * @param paragraphs the paragraphs to set
     */
    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public void addParagraph(Paragraph paragraph) {
        this.paragraphs.add(paragraph);
    }

    public List<Sentence> getSentences() {
        List<Sentence> sentences = new ArrayList<>(10);
        for(Paragraph paragraph : paragraphs) {
            sentences.addAll(paragraph.getSentences());
        }
        return sentences;
    }

    @Override
    public Iterator<Paragraph> iterator() {
        return paragraphs.iterator();
    }

    @Override
    public List<String> ngrams(int length) {
        List<String> ngrams = new ArrayList<>(10);
        for(Paragraph p : paragraphs) {
            ngrams.addAll(p.ngrams(length));
        }
        return ngrams;
    }

    /**
     * @return the similarDocs
     */
    public Map<Document, Double> getSimilarDocs() {
        return similarDocs;
    }

    /**
     * @param similarDocs the similarDocs to set
     */
    public void setSimilarDocs(Map<Document, Double> similarDocs) {
        this.similarDocs = similarDocs;
    }

    public void addSimilarDoc(Document document, double similarity) {
        similarDocs.put(document, similarity);
    }

}
