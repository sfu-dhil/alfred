/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.reader;

import ca.nines.alfred.entity.Document;
import ca.nines.alfred.entity.Paragraph;
import ca.nines.alfred.entity.Sentence;
import ca.nines.alfred.nlp.CoreNLPBuilder;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author michael
 */
public class HtmlReader {

    private final DocumentBuilder db;

    public HtmlReader() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        db = factory.newDocumentBuilder();
    }

    public Document read(final Path path) throws SAXException, IOException, XPathExpressionException {
        org.w3c.dom.Document xml = db.parse(path.toFile());
        Document doc = new Document();
        XPath xpath = XPathFactory.newInstance().newXPath();
        StanfordCoreNLP pipeline = CoreNLPBuilder.getInstance();

        doc.setFilename(path.toString());
        doc.setId((String)xpath.evaluate("/html/@id", xml.getDocumentElement(), XPathConstants.STRING));
        doc.setText(xml.getTextContent());

        NodeList pl = (NodeList)xpath.evaluate("//div[@lang='en']/p", xml.getDocumentElement(), XPathConstants.NODESET);
        for(int i = 0; i < pl.getLength(); i++) {
            Element p = (Element) pl.item(i);
            String paragraphId = p.getAttribute("id");
            p.normalize();
            Paragraph paragraph = new Paragraph();
            paragraph.setId(paragraphId);
            paragraph.setText(p.getTextContent());
            doc.addParagraph(paragraph);

            CoreDocument coredoc = new CoreDocument(p.getTextContent());
            pipeline.annotate(coredoc);
            int n = 1;
            for(CoreSentence coreSentence : coredoc.sentences()) {
                Sentence sentence = new Sentence();
                sentence.setData(coreSentence);
                sentence.setText(coreSentence.text());
                sentence.setId(paragraphId + "_" + n);
                paragraph.addSentence(sentence);
            }
        }

        return doc;
    }

    public Document read(final String filename) throws SAXException, IOException, XPathExpressionException {
        Path path = Paths.get(filename);
        return read(path);
    }
}
