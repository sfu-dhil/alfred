package ca.nines.alfred.entity;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

@RunWith(DataProviderRunner.class)
public class ReportBuildTest {

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
    public void build() throws IOException {
        assertNotNull(report);
    }

    @Test
    public void buildId() {
        assertTrue(report.hasId());
        assertEquals("lga_590", report.id);
    }

    @Test
    public void buildTitle() {
        assertEquals("Title", report.title);
    }

    @Test
    @UseDataProvider("buildMetadataData")
    public void buildMetadata(String expected, String key) {
        assertEquals(expected, report.metadata.get(key));
    }

    @DataProvider
    public static Object[][] buildMetadataData() {
        return new String[][]{
                {"fr", "dc.language"},
                {null, "dc.badkey"},
                {"1184", "wr.word-count"}
        };
    }

    @Test
    public void buildDocumentSimilarities() {
        assertEquals(1, report.documentSimilarities.size());
        DocumentSimilarity s = report.documentSimilarities.get(0);
        assertNotNull(s);
        assertEquals("gmm_23", s.getReportId());
        assertEquals("cos", s.getType());
        assertEquals(0.913978, s.getSimilarity(), 0.001);
    }

    @Test
    public void buildParagraphSimilarities() {
        assertEquals(1, report.paragraphSimilarities.size());
        List<ParagraphSimilarity> list = report.paragraphSimilarities.get("lga_590_2_tr");
        assertNotNull(list);
        assertEquals(1, list.size());
        ParagraphSimilarity s = list.get(0);
        assertEquals("abc_412", s.getReportId());
        assertEquals("abc_412_4", s.getParagraphId());
        assertEquals("vsm", s.getType());
        assertEquals(0.995, s.getSimilarity(), 0.001);
    }

    @Test
    public void buildContent() {
        assertThat(report.content, startsWith("mais je lavais presque"));
    }

    @Test
    public void buildTranslatedContent() {
        assertThat(report.translatedContent, startsWith("but i had almost anticipated"));
    }

//    @Test
//    public void buildParagraphs() {
//        assertEquals(2, report.paragraphs.size());
//        assertThat(report.paragraphs.get("lga_590_2_tr"), startsWith("but i had almost anticipated"));
//        assertEquals("paul roche", report.paragraphs.get("lga_590_13_tr"));
//    }

}