package ca.nines.alfred.entity;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class ReportSerializeTest {

    private Report report;

    @Before
    public void setUp() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/test-data/report.xml");
        String html = IOUtils.toString(in, StandardCharsets.UTF_8);
        report = Report.read(html);
    }

    @After
    public void tearDown() {
        report = null;
    }

    @Test
    public void serialize() {
        String html = report.serialize();
        assertNotNull(html);
        assertThat(html, startsWith("<?xml version=\"1.0\" "));
        Document document = Jsoup.parse(report.serialize());
        assertNotNull(document);
    }

    @Test
    public void serializeId() {
        Document document = Jsoup.parse(report.serialize());
        assertEquals("lga_590", document.selectFirst("html").id());

        report.setId("cheesypuffs");
        document = Jsoup.parse(report.serialize());
        assertEquals("cheesypuffs", document.selectFirst("html").id());
    }

    public void serializeMetadata() {
        // new key
        report.setMetadata("dc.chicanery", "yes");
        // changed key
        report.setMetadata("dc.language", "french");
        // removed key
        report.setMetadata("wr.translated", null);
        // removed key
        report.setMetadata("wr.word-count", "");

        Document document = Jsoup.parse(report.serialize());
        // unchanged key.
        assertEquals("1895-04-10", document.selectFirst("meta[name='dc.date']").attr("content"));
        assertEquals("yes", document.selectFirst("meta[name='dc.chicanery']").attr("content"));
        assertEquals("french", document.selectFirst("meta[name='dc.language']").attr("content"));
        assertNull(document.selectFirst("meta[name='wr.translated']"));
        assertNull(document.selectFirst("meta[name='dr.word-count']"));
    }

    @Test
    public void serializeDocumentSimilarities() {
        report.documentSimilarities.add(new DocumentSimilarity("a", 0.12, "tst"));
        report.documentSimilarities.remove(0);
        Document document = Jsoup.parse(report.serialize());

        assertEquals(1, document.select("link").size());
        Element link = document.selectFirst("link");
        assertEquals("a", link.attr("href"));
        assertEquals("similarity", link.attr("rel"));
        assertEquals("0.12", link.attr("data-similarity"));
        assertEquals("tst", link.attr("data-type"));
        assertTrue(link.hasClass("similarity"));
        assertTrue(link.hasClass("tst"));
    }

    @Test
    public void serializeParagraphSimilarities() {
        report.paragraphSimilarities.get("lga_590_2_tr").add(new ParagraphSimilarity("a", "b", 0.12, "tst"));
        report.paragraphSimilarities.get("lga_590_2_tr").remove(0);
        Document document = Jsoup.parse(report.serialize());

        assertEquals(1, document.select("a").size());
        Element link = document.selectFirst("a");
        assertEquals("a", link.attr("href"));
        assertEquals("0.12", link.attr("data-similarity"));
        assertEquals("b", link.attr("data-paragraph"));
        assertEquals("tst", link.attr("data-type"));
        assertTrue(link.hasClass("similarity"));
        assertTrue(link.hasClass("tst"));
    }
}
