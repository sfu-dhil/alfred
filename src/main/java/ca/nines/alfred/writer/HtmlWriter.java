/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.writer;

import ca.nines.alfred.entity.Document;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;

/**
 *
 * @author mjoyce
 */
public class HtmlWriter {

    public void write(Document document) throws ParserConfigurationException {
        this.write(document, System.out);
    }

    public void write(Document document, OutputStream out) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();

        Element html = (Element) doc.appendChild(doc.createElement("html"));
        html.setAttribute("id", document.getId());

        Element head = (Element) html.appendChild(doc.createElement("head"));
        
    }

}
