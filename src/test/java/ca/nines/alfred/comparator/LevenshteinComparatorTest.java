package ca.nines.alfred.comparator;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.nines.alfred.entity.TextCollection;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)

public class LevenshteinComparatorTest {

    @Test
    @UseDataProvider("compareData")
    public void levenshtein(double expected, String a, String b) {
        LevenshteinComparator comparator = new LevenshteinComparator(null, null);
        assertEquals(expected, comparator.levenshtein(a, b), 0.001);
    }

    @DataProvider
    public static Object[][] compareData() {
        return new Object[][] {
                {0.75, "abcd", "accd"},
                {0.75, "accd", "abcd"},
                {0.0, "abcd", ""},
                {0.0, "", "accd"},
                {0.0, "a", "abcdef"},
                {1.0, "abcd", "abcd"},
        };
    }

    @Test
    @UseDataProvider("compareData")
    public void compare(double expected, String a, String b) {
        TextCollection collection = mock(TextCollection.class);
        when(collection.get(eq("a1"))).thenReturn(a);
        when(collection.get(eq("a2"))).thenReturn(b);
        LevenshteinComparator comparator = new LevenshteinComparator(collection, null);
        assertEquals(expected, comparator.compare("a1", "a2"), 0.001);
    }

}