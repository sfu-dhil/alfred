package ca.nines.alfred.entity;

import ca.nines.alfred.util.Text;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Report {

    private File file;

    private Document document;

    private String id;

    private String title;

    private String content;

    private String translatedContent;

    private Map<String,String> metadata;

    private List<DocumentSimilarity> documentSimilarities;

    // id => content
    private Map<String, String> paragraphs;

    // paragraph id => list
    private Map<String, List<ParagraphSimilarity>> paragraphSimilarities;

    public Report() {
        metadata = new TreeMap<>();
        documentSimilarities = new ArrayList<>();
        paragraphs = new HashMap<>();
        paragraphSimilarities = new HashMap<>();
    }

    public static Report read(File file) throws IOException {

        InputStream inputStream = FileUtils.openInputStream(file);
        Document document = Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser());
        inputStream.close();

        Report report = new Report();
        report.file = file;
        report.document = document;
        report.id = document.selectFirst("html").attr("id");
        report.title = document.title();

        for(Element meta : document.select("meta")) {
            report.metadata.put(meta.attr("name"), meta.attr("content"));
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

        report.content = Text.normalize(document.getElementById("original").text());
        Element div = document.selectFirst("#translation");
        if(div != null) {
            report.translatedContent = Text.normalize(div.text());
        }

        Elements paragraphs = null;
        if(report.getMetadata("dc.language").equals("en")) {
            paragraphs = document.select("#original p");
        } else {
            paragraphs = document.select("#translation p");
        }
        for(Element p : paragraphs) {
            report.paragraphs.put(p.id(), Text.normalize(p.text()));
        }

        return report;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasId() {
        return this.id != null;
    }

    public void setParagraphIds() {
        int m = 0;
        for(Element p : document.select("#original p")) {
            p.attr("id", this.id + "_" + m);
            m++;
        }
    }

    public String[] getParagraphIds() {
        return paragraphs.keySet().toArray(new String[paragraphs.size()]);
    }

    public String getParagraph(String id) {
        return paragraphs.get(id);
    }

    public String getContent() {
        return content;
    }

    public String getContentHtml() {
        return document.select("div#original").outerHtml();
    }

    public String getComparableContent() {
        if(metadata.get("dc.language").equals("en")) {
            return content;
        } else {
            return translatedContent;
        }
    }

    public String getTranslatedContent() {
        return translatedContent;
    }

    public String getTitle() {
        return title;
    }

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

    public void setMetadata(String name, String content) {
        if(content == null) {
            metadata.remove(name);
        } else {
            metadata.put(name, content);
        }
    }

    public String getMetadata(String name) {
        return metadata.get(name);
    }

    public boolean hasMetadata(String name) {
        return metadata.containsKey(name);
    }

    public File getFile() {
        return file;
    }

    public void addDocumentSimilarity(DocumentSimilarity s) {
        documentSimilarities.add(s);
    }

    public void removeDocumentSimilarities() {
        documentSimilarities.clear();
    }

    public void addParagraphSimilarity(String id, ParagraphSimilarity s) {
        if( ! paragraphSimilarities.containsKey(id)) {
            paragraphSimilarities.put(id, new ArrayList<>());
        }
        paragraphSimilarities.get(id).add(s);
    }

    public void removeParagraphSimilarities() {
        paragraphSimilarities.clear();
    }

    public String serialize() {
        document.selectFirst("html").attr("id", id);

        document.select("meta").remove();
        document.charset(StandardCharsets.UTF_8);
        for(Map.Entry<String,String> entry : metadata.entrySet()) {
            if(entry.getKey().isEmpty() || entry.getValue().isEmpty()) {
                continue;
            }
            Element meta = new Element("meta");
            meta.attr("content", entry.getValue());
            meta.attr("name", entry.getKey());
            meta.appendTo(document.head());
        }

        document.select(".similarity").remove();
        for(DocumentSimilarity s : documentSimilarities) {
            Element link = new Element("link");
            link.attr("href", s.getReportId());
            link.addClass("similarity").addClass(s.getType());
            link.attr("rel", "similarity");
            link.attr("data-similarity", "" + s.getSimilarity());
            link.attr("data-type", s.getType());
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
                a.appendTo(p);
            }
        }

        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().prettyPrint(true);
        document.normalise();
        return document.html();
    }

}
