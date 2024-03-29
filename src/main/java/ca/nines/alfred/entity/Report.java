/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package ca.nines.alfred.entity;

import ca.nines.alfred.util.Text;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.*;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A report object is the metadata and content of one report file stored on disk.
 */
public class Report {

    /**
     * The source file for this report.
     */
    File file;

    /**
     * A JSoup document with the report's content.
     */
    Document document;

    /**
     * Errors during parsing.
     */
    List<String> errors;

    /**
     * Report ID from the html root element.
     */
    String id;

    /**
     * Title from the report title element
     */
    String title;

    /**
     * Normalized text content. The text is converted to lower case and stripped of punctuation suitable for matching.
     */
    String content;

    /**
     * Normalized translated text content. The text is converted to lower case and stripped of punctuation suitable for matching.
     */
    String translatedContent;

    /**
     * Metadata elements parsed from the HTML document.
     */
    Map<String,String> metadata;

    /**
     * Document-level similarities either found in the corpus or in the document itself.
     */
    List<DocumentSimilarity> documentSimilarities;

    /**
     * List of IDs to normalized paragraph content.
     */
    Map<String, String> paragraphs;

    String signature;

    /**
     * List of IDs mapped to paragraph similarities
     */
    Map<String, List<ParagraphSimilarity>> paragraphSimilarities;

    /**
     * Create a new empty report.
     */
    public Report() {
        metadata = new TreeMap<>();
        documentSimilarities = new ArrayList<>();
        paragraphs = new HashMap<>();
        paragraphSimilarities = new HashMap<>();
        errors = new ArrayList<>();
    }

    /**
     * Read a report from a file on disk.
     *
     * @param file the file to read
     * @return the parsed report
     * @throws IOException if the report cannot be read
     */
    public static Report read(File file) throws IOException {
        String html = FileUtils.readFileToString(file, "UTF-8");
        Report report = null;
        try {
            report = read(html);
            report.file = file;
            return report;
        } catch (Exception exception) {
            System.err.println("Cannot read " + file.getPath() + " due to error: " + exception.getMessage());
            exception.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Read a report from a string.
     *
     * @param html the string to be parsed
     * @return the parsed report
     */
    public static Report read(String html) {
        String data = html.replace("\u2028", "");
        Parser parser = Parser.xmlParser();
        parser.setTrackErrors(100);

        Document document = Jsoup.parse(data, "", parser);
        ParseErrorList errors = parser.getErrors();

        Report report = new Report();
        report.document = document;
        for(ParseError e : errors) {
            report.errors.add(e.toString());
        }
        report.id = document.selectFirst("html").attr("id");
        report.title = document.title();

        for(Element meta : document.select("meta")) {
            report.metadata.put(meta.attr("name"), meta.attr("content"));
        }

        Element sig = document.selectFirst("p.signature");
        if(sig != null) {
            report.signature = sig.text();
        }

        for(Element link : document.select("link")) {
            DocumentSimilarity s = new DocumentSimilarity(
                    link.attr("href"),
                    Double.valueOf(link.attr("data-similarity")),
                    link.attr("data-type"));
            report.documentSimilarities.add(s);
        }

        for(Element p : document.select("p")) {
            if(p.select("a").isEmpty()) {
                continue;
            }
            List<ParagraphSimilarity> similarities = new ArrayList<>();
            for(Element a : p.select("a")) {
                ParagraphSimilarity s = new ParagraphSimilarity(
                        a.attr("href"),
                        a.attr("data-paragraph"),
                        Double.valueOf(a.attr("data-similarity")),
                        a.attr("data-type")
                );
                similarities.add(s);
            }
           report.paragraphSimilarities.put(p.id(), similarities);
        }

        if(document.getElementById("original") == null) {
            report.errors.add("Cannot find original text.");
        } else {
            report.content = Text.normalize(document.getElementById("original").select("p:not(.heading)").text());
        }
        Element div = document.selectFirst("#translation");
        if(div != null) {
            report.translatedContent = Text.normalize(div.text());
        }

        Elements paragraphs = document.select("#original p");
        for(Element p : paragraphs) {
            report.paragraphs.put(p.id(), Text.normalize(p.text()));
        }

        return report;
    }

    /**
     * Get report ID parsed from the HTML element.
     *
     * @return the ID or null
     */
    public String getId() {
        return id;
    }

    /**
     * Set the report ID
     *
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public void generateId(int n) {
        String parent = file.getParent();
        parent = parent.substring(parent.indexOf('/')+1);
        StringBuilder initials = new StringBuilder();
        for(String s : parent.toLowerCase().split("[ /_]")) {
            initials.append(s.charAt(0));
        }
        id = initials + "_" + n;
    }

    /**
     * Check if the report has an ID
     *
     * @return true if the report has an ID.
     */
    public boolean hasId() {
        return this.id != null;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public List<String> getErrors() {
        return errors;
    }

    /**
     * Set or reset the paragraph IDs for the paragraphs in the original content of the documentation.
     */
    public void setParagraphIds() {
        int m = 0;
        for(Element p : document.select("#original p")) {
            p.attr("id", this.id + "_" + m);
            m++;
        }
    }

    /**
     * Return the paragraph IDs. The order is not based on document structure.
     *
     * @return an array of paragraph IDs.
     */
    public String[] getParagraphIds() {
        return getParagraphIds(true);
    }

    public String[] getParagraphIds(boolean all) {
        if(all) {
            return paragraphs.keySet().toArray(new String[paragraphs.size()]);
        }
        List<String> ids = new ArrayList<>();
        for(String id : paragraphs.keySet()) {
            if(document.selectFirst("#" + id).hasClass("heading")) {
                continue;
            }
            ids.add(id);
        }
        return ids.toArray(new String[ids.size()]);
    }

    public void removeParagraphIds() {
        for(Element e : document.select("p")) {
            if(e.hasAttr("id")) {
                e.removeAttr("id");
            }
        }
    }

    /**
     * Get the normalized content of one paragraph.
     *
     * @param id of the paragraph to fetch
     * @return normalized content from the paragraph
     */
    public String getParagraph(String id) {
        return paragraphs.get(id);
    }

    public String getSignature() {
        return signature;
    }

    /**
     * Get the normalized original content of the report.
     *
     * @return normalized original content
     */
    public String getContent(boolean normalized) {
        if(normalized) {
            return content;
        } else {
            return document.select("div#original").select("p:not(.heading)").text();
        }
    }

    public String getContent() {
        return getContent(true);
    }

    /**
     * Get the original HTML content, unnormalized. As it appears in the XML file on disk.
     *
     * @return HTML-containing string
     */
    public String getContentHtml() {
        return document.select("div#original").outerHtml();
    }

    /**
     * Returns the English-language normalized content from the report.
     *
     * @return a comparable string
     */
    public String getComparableContent() {
        if(metadata.get("dc.language").equals("en")) {
            return content;
        } else {
            return translatedContent;
        }
    }

    /**
     * Get the title of the report
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set or replace the translated content.
     *
     * @param translation html string with the translated content.
     */
    public void setTranslation(String translation) {
        document.select("#translation").remove();
        document.select(".translated").remove();
        translatedContent = null;
        setMetadata("wr.translated", null);

        if(translation == null) {
            return;
        }

        Document div = Jsoup.parseBodyFragment(translation);
        div.select("#original")
                .attr("id", "translation")
                .attr("lang", "en");

        for(Element p : div.select("p")) {
            p.attr("id", p.attr("id") + "_tr");
        }

        div.body().appendTo(document.body());
        translatedContent = Text.normalize(div.text());
        setMetadata("wr.translated", "yes");
    }

    /**
     * Set a metadata item for the report. If content is null or the empty string the
     * metadata key will be removed.
     *
     * @param name the name attribute of the metadata item
     * @param content the content of the metadata item
     */
    public void setMetadata(String name, String content) {
        if(content == null) {
            metadata.remove(name);
        } else {
            metadata.put(name, content);
        }
    }

    /**
     * Get a metadata item.
     *
     * @param name the metadata key
     * @return the value of the metadata key or null if the key doesn't exit.
     */
    public String getMetadata(String name) {
        return metadata.get(name);
    }

    /**
     * Check if a metadata key exists.
     *
     * @param name name of the key
     * @return true if the key exists
     */
    public boolean hasMetadata(String name) {
        return metadata.containsKey(name);
    }

    public String getLanguage() {
        return metadata.get("dc.language");
    }

    public Elements getElements() {
        return document.select("*");
    }

    /**
     * Get the source file for the report
     *
     * @return the file or null if the report was built from a string
     */
    public File getFile() {
        return file;
    }

    /**
     * Add a document-level similarity.
     *
     * @param s the similarity to add
     */
    public void addDocumentSimilarity(DocumentSimilarity s) {
        documentSimilarities.add(s);
    }

    /**
     * Remove all document level similarities.
     */
    public void removeDocumentSimilarities() {
        documentSimilarities.clear();
    }

    /**
     * Add a paragraph similarity to the report.
     *
     * @param id the id of the paragraph that is similar to one in another document
     * @param s the paragraph similarity
     */
    public void addParagraphSimilarity(String id, ParagraphSimilarity s) {
        if( ! paragraphSimilarities.containsKey(id)) {
            paragraphSimilarities.put(id, new ArrayList<>());
        }
        paragraphSimilarities.get(id).add(s);
    }

    /**
     * Remove all paragraph level similarities from the report.
     */
    public void removeParagraphSimilarities() {
        paragraphSimilarities.clear();
    }

    public List<String> checkStructure() throws XPathExpressionException {
        List<String> errors = new ArrayList<>();

        org.w3c.dom.Document dom = W3CDom.convert(document);

        NamespaceContext nc = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return prefix.equals("html") ? "http://www.w3.org/1999/xhtml" : null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return namespaceURI.equals("http://www.w3.org/1999/xhtml") ? "html" : null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return Arrays.stream(new String[]{"html"}).iterator();
            }
        };

        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(nc);

        String expr = "//html:body//text()[not(ancestor::html:p) and not(ancestor::html:h1)][normalize-space(.)]";
        NodeList nl = (NodeList)xPath.compile(expr).evaluate(dom, XPathConstants.NODESET);

        for(int i = 0; i < nl.getLength(); i++) {
            String s = nl.item(i).getTextContent().strip();
            if( ! s.isEmpty()) {
                errors.add("Text found outside of paragraph or heading: \"" + s + "\"");
            }
        }

        expr = "//html:body//*[not(ancestor-or-self::html:div)]";
        nl = (NodeList)xPath.evaluate(expr, dom, XPathConstants.NODESET);
        if(nl.getLength() > 0) {
            errors.add("Content found outside of a div");
        }

        return errors;
    }

    /**
     * Serialize the report into html, after incorporating any changes to the metadata or similarity
     * data.
     *
     * @return HTML string
     */
    public String serialize() {
        if(id != null) {
            document.selectFirst("html").attr("id", id);
        } else {
            document.selectFirst("html").removeAttr("id");
        }

        document.select("meta").remove();
        document.charset(StandardCharsets.UTF_8);
        for(Map.Entry<String,String> entry : metadata.entrySet()) {
            if(entry.getKey().isEmpty() || entry.getValue().isEmpty()) {
                continue;
            }
            Element meta = new Element("meta");
            meta.attr("content", entry.getValue());
            meta.attr("name", entry.getKey());
            if(entry.getKey().equals("dc.publisher")) {
                meta.attr("data-sortable", this.getMetadata("dc.publisher.sortable"));
            }
            meta.appendTo(document.head());
        }

        // Remove all link and a elements and replace them with new creations.
        document.select(".similarity").remove();

        for(DocumentSimilarity s : documentSimilarities) {
            Element link = new Element("link");
            link.attr("href", s.getReportId());
            link.addClass("similarity").addClass(s.getType());
            link.attr("rel", "similarity");
            link.attr("data-similarity", "" + s.getSimilarity());
            link.attr("data-type", s.getType());
            if(s.getPaperId() != null) {
                link.attr("data-paper-id", s.getPaperId());
            }
            link.appendTo(document.head());
        }

        for(Element p : document.select("p")) {
            if( ! paragraphSimilarities.containsKey(p.id())) {
                continue;
            }
            for(ParagraphSimilarity s : paragraphSimilarities.get(p.id())) {
                Element a = new Element("a");
                a.attr("href", s.getReportId());
                a.addClass("similarity").addClass(s.getType());
                a.attr("data-document", s.getReportId());
                a.attr("data-paragraph", s.getParagraphId());
                a.attr("data-similarity", "" + s.getSimilarity());
                a.attr("data-type", s.getType());
                if(s.getPaperId() != null) {
                    a.attr("data-paper-id", s.getPaperId());
                }
                a.appendTo(p);
            }
        }

        document.childNodes().stream().filter(n -> n instanceof DocumentType).findFirst().ifPresent(Node::remove);
        document.normalise();
        document.outputSettings().charset(StandardCharsets.UTF_8);
        document.outputSettings().indentAmount(2);
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().prettyPrint(true);
        return document.html();
    }

    public List<DocumentSimilarity> getDocumentSimilarities() {
        return this.documentSimilarities;
    }

    public List<ParagraphSimilarity> getParagraphSimilarities() {
        List<ParagraphSimilarity> similarities = new ArrayList<>();
        for(List<ParagraphSimilarity> lp : paragraphSimilarities.values()) {
            similarities.addAll(lp);
        }
        return similarities;
    }

    public List<ParagraphSimilarity> getParagraphSimilarities(String id) {
        List<ParagraphSimilarity> similarities = new ArrayList<>();
        if(paragraphSimilarities.get(id) == null) {
            return similarities;
        }
        similarities.addAll(paragraphSimilarities.get(id));
        return similarities;
    }

    public String getText() {
        return document.text();
    }
}
