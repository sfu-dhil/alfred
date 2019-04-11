package ca.nines.alfred.comparator;

import ca.nines.alfred.tokenizer.WordTokenizer;
import ca.nines.alfred.util.Settings;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class VsmComparatorTest {

    private VsmComparator comparator;

    @Before
    public void setUp() {
        Settings settings = Settings.getInstance();
        // The test settings file doesn't have these settings.
        settings.set("vsm_threshold", "0.2");
        settings.set("min_length", "5");
        comparator = new VsmComparator(new WordTokenizer());
    }

    @Test
    public void add() {
        comparator.add("a", "I am the very model of a very model general.");
        assertEquals(1, comparator.model.size());
        Map<String, Double> counts = comparator.model.get("a");
        assertEquals(8, counts.size());
        // No call to complete - no weights calculated yet.
        assertEquals(1, counts.get("I").intValue());
        assertEquals(1, counts.get("the").intValue());
        assertEquals(2, counts.get("very").intValue());
        assertEquals(2, counts.get("model").intValue());
    }

    @Test(expected = RuntimeException.class)
    public void addAfterComplete() {
        comparator.add("a", "I am the very model of a very model general.");
        comparator.complete();
        comparator.add("b", "I am also a general.");
    }

    @Test
    public void addShort() {
        comparator.add("a", "I am");
        assertEquals(0, comparator.model.size());
    }

    @Test
    public void addDuplicate() {
        comparator.add("a", "I am a cheeseburger");
        comparator.add("a", "I am a cheesy cheeseburger");
        assertEquals(1, comparator.model.size());
        assertNull(comparator.model.get("a").get("cheesy"));
    }

    @Test
    public void addDocCounter() {
        comparator.add("a", "I am a cheeseburger");
        comparator.add("b", "I am a cheesy cheeseburger");
        assertEquals(2, comparator.model.size());

        Map<String,Integer> docCounter = comparator.docTermCounts;
        assertEquals(5, docCounter.size());
        assertEquals(2, docCounter.get("I").intValue());
        assertEquals(2, docCounter.get("cheeseburger").intValue());
        assertEquals(1, docCounter.get("cheesy").intValue());
    }

    @Test
    public void addTermCounter() {
        comparator.add("a", "I am a very very cheeseburger");
        comparator.add("b", "I am a cheesy cheeseburger");
        assertEquals(2, comparator.model.size());

        Map<String,Double> ma = comparator.model.get("a");
        assertEquals(1, ma.get("am").intValue());
        assertEquals(2, ma.get("very").intValue());

        Map<String,Double> mb = comparator.model.get("b");
        assertEquals(1, mb.get("am").intValue());
        assertEquals(1, mb.get("cheesy").intValue());
    }

    @Test
    public void complete() {
        // example from http://www.minerazzi.com/tutorials/term-vector-3.pdf
        comparator.add("a", "shipment of gold damaged in a fire");
        comparator.add("b", "delivery of silver arrived in a silver truck");
        comparator.add("c", "shipment of gold arrived in a truck");
        comparator.complete();

        assertEquals(3, comparator.model.size());

        Map<String,Double> wa = comparator.model.get("a");
        assertEquals(0, wa.get("a"), 0.01);
        assertNull(wa.get("arrived"));
        assertEquals(0.48, wa.get("damaged"), 0.01);
        assertNull(wa.get("silver"));

        Map<String,Double> wb = comparator.model.get("b");
        assertEquals(0, wb.get("a"), 0.01);
        assertEquals(0.18, wb.get("arrived"), 0.01);
        assertNull(wb.get("damaged"));
        assertEquals(0.96, wb.get("silver"), 0.01);

        Map<String,Double> wc = comparator.model.get("c");
        assertEquals(0, wc.get("a"), 0.01);
        assertEquals(0.18, wc.get("arrived"), 0.01);
        assertNull(wc.get("damaged"));
        assertNull(wc.get("silver"));
    }

    @Test
    public void compare() {
        // example from http://www.minerazzi.com/tutorials/term-vector-3.pdf
        comparator.add("a", "shipment of gold damaged in a fire");
        comparator.add("b", "delivery of silver arrived in a silver truck");
        comparator.add("c", "shipment of gold arrived in a truck");
        comparator.complete();

        // not similar
        assertEquals(0.0, comparator.compare("a", "b"), 0.001);

        // similar
        assertEquals(0.24, comparator.compare("a", "c"), 0.01);

        // below threshold - would be 0.16.
        assertEquals(0.0, comparator.compare("b", "c"), 0.01);
    }
}
