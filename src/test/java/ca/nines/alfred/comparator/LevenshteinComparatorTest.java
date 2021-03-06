package ca.nines.alfred.comparator;

import ca.nines.alfred.util.Settings;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LevenshteinComparatorTest {

    private LevenshteinComparator comparator;

    @Before
    public void setUp() {
        Settings settings = Settings.getInstance();
        // The test settings file doesn't have these settings.
        settings.set("lev_threshold", "0.6");
        settings.set("min_length", "5");

        comparator = new LevenshteinComparator();
    }

    @Test
    public void add() {
        comparator.add("a", "I am the very model of a general.");
        assertEquals(1, comparator.text.size());
        assertTrue(comparator.text.containsKey("a"));
        assertEquals("I am the very model of a general.", comparator.text.get("a"));
    }

    @Test
    public void addShort() {
        comparator.add("a", "I am");
        assertEquals(0, comparator.text.size());
    }

    @Test
    public void compare() {
        comparator.add("a", "I am the very model of a general.");
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
    public void compareDifferentSizes() {
        comparator.add("a", "I am the very model of a modern major general.");
        comparator.add("b", "I am the general.");
        assertEquals(0.0, comparator.compare("a", "b"), 0.001);
    }

    @Test
    public void compareDifferentTexts() {
        comparator.add("a", "I am the very model of a modern major general.");
        comparator.add("b", "Let me know what you think -- and thanks for.");
        assertEquals(0.0, comparator.compare("a", "b"), 0.001);
    }

}