/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.entity;

import ca.nines.alfred.reader.HtmlReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author michael
 */
public class Corpus implements Iterable<Document>, NGrams {

    private List<Document> documents;

    public Corpus() {
        documents = new ArrayList<>(5);
    }

    public static Corpus build(String filepath) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HtmlReader reader = new HtmlReader();
        Corpus corpus = new Corpus();
        Path path = Paths.get(filepath);
        Stream<Path> filePathStream = Files.find(path, 4, (fspath, attrs) -> {return attrs.isRegularFile();});
        List<Path> pathList = filePathStream.collect(Collectors.toCollection(ArrayList::new));
        for(Path p : pathList) {
            corpus.addDocument(reader.read(p));
        }
        return corpus;
    }

    /**
     * @return the documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * @param documents the documents to set
     */
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document document) {
        documents.add(document);
    }

    public Document get(int i) {
        return documents.get(i);
    }

    @Override
    public Iterator<Document> iterator() {
        return documents.iterator();
    }

    @Override
    public List<String> ngrams(int length) {
        List<String> ngrams = new ArrayList<>(10);
        for(Document d : documents) {
            ngrams.addAll(d.ngrams(length));
        }
        return ngrams;
    }

    public int size() {
        return documents.size();
    }

}
