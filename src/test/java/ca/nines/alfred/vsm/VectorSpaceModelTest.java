package ca.nines.alfred.vsm;

import ca.nines.alfred.util.Tokenizer;
import org.junit.Test;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class VectorSpaceModelTest {

    @Test
    public void add() {
        Tokenizer tokenizer = mock(Tokenizer.class);
        when(tokenizer.tokenize(eq("a b c a"))).thenReturn(
                Arrays.asList(new String[]{"a", "b", "c", "a"})
        );
        VectorSpaceModel vsm = new VectorSpaceModel(tokenizer);
        vsm.add("a1", "a b c a");
        assertEquals(1, vsm.docTermCounts.get("a").intValue());
        assertEquals(1, vsm.docTermCounts.get("b").intValue());
        assertEquals(1, vsm.docTermCounts.get("c").intValue());

        assertEquals(2.0, vsm.model.get("a1").get("a"), 0.01);
        assertEquals(1.0, vsm.model.get("a1").get("b"), 0.01);
        assertEquals(1.0, vsm.model.get("a1").get("c"), 0.01);
    }

    @Test
    public void addTwoDocuments() {
        Tokenizer tokenizer = mock(Tokenizer.class);
        when(tokenizer.tokenize(eq("a b c a"))).thenReturn(
                Arrays.asList(new String[]{"a", "b", "c", "a"})
        );
        when(tokenizer.tokenize(eq("a c c a d"))).thenReturn(
                Arrays.asList(new String[]{"a", "c", "c", "a", "d"})
        );

        VectorSpaceModel vsm = new VectorSpaceModel(tokenizer);
        vsm.add("a1", "a b c a");
        vsm.add("a2", "a c c a d");

        assertEquals(2, vsm.docTermCounts.get("a").intValue());
        assertEquals(1, vsm.docTermCounts.get("b").intValue());
        assertEquals(2, vsm.docTermCounts.get("c").intValue());

        assertEquals(2.0, vsm.model.get("a1").get("a"), 0.01);
        assertEquals(1.0, vsm.model.get("a1").get("b"), 0.01);
        assertEquals(1.0, vsm.model.get("a1").get("c"), 0.01);

        assertEquals(2.0, vsm.model.get("a2").get("a"), 0.01);
        assertEquals(2.0, vsm.model.get("a2").get("c"), 0.01);
        assertEquals(1.0, vsm.model.get("a2").get("d"), 0.01);
    }

    @Test
    public void computeWeights() {
        Tokenizer tokenizer = mock(Tokenizer.class);
        when(tokenizer.tokenize(eq("a b c a"))).thenReturn(
                Arrays.asList(new String[]{"a", "b", "b", "c", "a"})
        );
        when(tokenizer.tokenize(eq("a c c a d"))).thenReturn(
                Arrays.asList(new String[]{"a", "c", "c", "a", "d"})
        );

        VectorSpaceModel vsm = new VectorSpaceModel(tokenizer);
        vsm.add("a1", "a b c a");
        vsm.add("a2", "a c c a d");
        vsm.computeWeights();

        assertEquals(0.0, vsm.model.get("a1").get("a"), 0.01);
        assertEquals(0.6020, vsm.model.get("a1").get("b"), 0.001);
        assertEquals(0.0, vsm.model.get("a1").get("c"), 0.01);

        assertEquals(0.0, vsm.model.get("a2").get("a"), 0.01);
        assertEquals(0.0, vsm.model.get("a2").get("c"), 0.01);
        assertEquals(0.3010, vsm.model.get("a2").get("d"), 0.001);
    }

    @Test
    public void realExample() throws IOException {
        Tokenizer tokenizer = new Tokenizer();
        VectorSpaceModel vsm = new VectorSpaceModel(tokenizer);
        vsm.add("d1", "shipment of gold damaged in a fire");
        vsm.add("d2", "delivery of silver arrived in a silver truck");
        vsm.add("d3", "shipment of gold arrived in a truck");
        vsm.computeWeights();
        assertEquals(0.0, vsm.model.get("d1").get("a"), 0.01);
        assertEquals(0.0, vsm.model.get("d2").get("a"), 0.01);
        assertEquals(0.0, vsm.model.get("d3").get("a"), 0.01);

        assertNull(vsm.model.get("d1").get("arrived"));
        assertEquals(0.18, vsm.model.get("d2").get("arrived"), 0.01);
        assertEquals(0.18, vsm.model.get("d3").get("arrived"), 0.01);

        assertEquals(0.48, vsm.model.get("d1").get("damaged"), 0.01);
        assertNull(vsm.model.get("d2").get("damaged"));
        assertNull(vsm.model.get("d3").get("damaged"));

        assertNull(vsm.model.get("d1").get("delivery"));
        assertEquals(0.48, vsm.model.get("d2").get("delivery"), 0.01);
        assertNull(vsm.model.get("d3").get("delivery"));

        assertEquals(0.48, vsm.model.get("d1").get("fire"), 0.01);
        assertNull(vsm.model.get("d2").get("fire"));
        assertNull(vsm.model.get("d3").get("fire"));

        assertEquals(0.18, vsm.model.get("d1").get("gold"), 0.01);
        assertNull(vsm.model.get("d2").get("gold"));
        assertEquals(0.18, vsm.model.get("d3").get("gold"), 0.01);

        assertNull(vsm.model.get("d1").get("silver"));
        assertEquals(0.95, vsm.model.get("d2").get("silver"), 0.01);
        assertNull(vsm.model.get("d3").get("silver"));

        assertEquals(0.18, vsm.model.get("d1").get("shipment"), 0.01);
        assertNull(vsm.model.get("d2").get("shipment"));
        assertEquals(0.18, vsm.model.get("d3").get("shipment"), 0.01);

        assertNull(vsm.model.get("d1").get("truck"));
        assertEquals(0.18, vsm.model.get("d2").get("truck"), 0.01);
        assertEquals(0.18, vsm.model.get("d3").get("truck"), 0.01);

        assertEquals(0.0, vsm.compare("d1", "d2"), 0.001);
        assertEquals(0.2448, vsm.compare("d1", "d3"), 0.001);
        assertEquals(0.1607, vsm.compare("d2", "d3"), 0.001);
    }

}