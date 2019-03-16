package ca.nines.alfred.entity;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ReportTest {

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
    public void getId() {
        assertEquals("lga_590", report.getId());
    }

    @Test
    public void setId() {
        report.setId("abc");
        assertEquals("abc", report.id);
    }

    @Test
    public void hasId() {
        assertTrue(report.hasId());
        report.id = null;
        assertFalse(report.hasId());
    }

    @Test
    public void setParagraphIds() {
    }

    @Test
    public void getParagraphIds() {
    }

    @Test
    public void getParagraph() {
    }

    @Test
    public void getContent() {
    }

    @Test
    public void getContentHtml() {
    }

    @Test
    public void getComparableContent() {
    }

    @Test
    public void getTitle() {
    }

    @Test
    public void setTranslation() {
    }

    @Test
    public void setMetadata() {
    }

    @Test
    public void getMetadata() {
    }

    @Test
    public void hasMetadata() {
    }

    @Test
    public void getFile() {
    }

    @Test
    public void addDocumentSimilarity() {
    }

    @Test
    public void removeDocumentSimilarities() {
    }

    @Test
    public void addParagraphSimilarity() {
    }

    @Test
    public void removeParagraphSimilarities() {
    }

    @Test
    public void serialize() {
    }
}