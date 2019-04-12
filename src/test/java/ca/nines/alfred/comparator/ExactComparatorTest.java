package ca.nines.alfred.comparator;

import ca.nines.alfred.util.Settings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExactComparatorTest {

    private ExactComparator comparator;

    @Before
    public void setUp() {
        Settings settings = Settings.getInstance();
        // The test settings file doesn't have this setting.
        settings.set("min_length", "5");

        comparator = new ExactComparator();
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
        assertEquals(0.0, comparator.compare("a", "b"), 0.001);
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

}