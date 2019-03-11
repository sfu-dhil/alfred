package ca.nines.alfred.entity;

import ca.nines.alfred.util.Text;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Report {

    private File file;

    private Document document;

    private String id;

    private String content;

    private String translatedContent;

    private Map<String,String> metadata;

    private List<DocumentSimilarity> documentSimilarities;

    public Report() {
        metadata = new TreeMap<>();
        documentSimilarities = new ArrayList<>();
    }

    public static Report read(File file) throws IOException {

        InputStream inputStream = FileUtils.openInputStream(file);
        Document document = Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser());
        inputStream.close();

        Report report = new Report();
        report.file = file;
        report.document = document;
        report.id = document.selectFirst("html").attr("id");

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

        report.content = Text.normalize(document.getElementById("original").text());
        Element div = document.selectFirst("#translation");
        if(div != null) {
            report.translatedContent = Text.normalize(div.text());
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

    public void removeSimilarities() {
        documentSimilarities.clear();
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

        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().prettyPrint(true);
        document.normalise();
        return document.html();
    }

}
