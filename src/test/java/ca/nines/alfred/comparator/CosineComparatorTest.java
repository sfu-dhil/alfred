package ca.nines.alfred.comparator;

import ca.nines.alfred.tokenizer.Tokenizer;
import ca.nines.alfred.tokenizer.WordNGramTokenizer;
import ca.nines.alfred.tokenizer.WordTokenizer;
import ca.nines.alfred.util.Settings;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class CosineComparatorTest {

    private CosineComparator comparator;

    @Before
    public void setUp() {
        Settings settings = Settings.getInstance();
        // The test settings file doesn't have these settings.
        settings.set("cos_threshold", "0.6");
        settings.set("min_length", "5");
        comparator = new CosineComparator(new WordTokenizer());
    }

    @Test
    public void add() {
        comparator.add("a", "I am the very model of a very model general.");
        assertEquals(1, comparator.termCount.size());
        Map<String, Integer> counts = comparator.termCount.get("a");
        assertEquals(8, counts.size());
        assertEquals(1, counts.get("I").intValue());
        assertEquals(1, counts.get("the").intValue());
        assertEquals(2, counts.get("very").intValue());
        assertEquals(2, counts.get("model").intValue());
    }

    @Test
    public void addShort() {
        comparator.add("a", "I am");
        assertEquals(0, comparator.termCount.size());
    }

    @Test
    public void addDuplicate() {
        comparator.add("a", "I am a cheeseburger");
        comparator.add("a", "I am a cheesy cheeseburger");
        assertEquals(1, comparator.termCount.size());
        assertNull(comparator.termCount.get("a").get("cheesy"));
    }

    @Test
    public void compare() {
        comparator.add("a", "I am the very model of a general");
        comparator.add("b", "I am the very model of a modern general.");
        assertEquals(0.825, comparator.compare("a", "b"), 0.001);
    }

    @Test
    public void compareEqual() {
        comparator.add("a", "I am the very model of a general.");
        assertEquals(1.0, comparator.compare("a", "a"), 0.001);
    }

    @Test
    public void compareNonExistent() {
        comparator.add("a", "I am the very model of a general.");
        assertEquals(0.0, comparator.compare("a", "cheese"), 0.001);
    }

    @Test
    public void compareDifferentTexts() {
        comparator.add("a", "I am the very model of a modern major general");
        comparator.add("b", "Let me know what you think and thanks for all the fish");
        assertEquals(0.0912, comparator.compare("a", "b"), 0.001);
    }
}