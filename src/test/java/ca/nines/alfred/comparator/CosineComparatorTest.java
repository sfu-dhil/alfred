package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.TextCollection;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)

public class CosineComparatorTest {

    @Test
    @UseDataProvider("compareData")
    public void cosine(double expected, String a, String b) {
        CosineComparator comparator = new CosineComparator(null, null);
        assertEquals(expected, comparator.cosine(a, b), 0.001);
    }

    @DataProvider
    public static Object[][] compareData() {
        return new Object[][] {
                {0.9899, "a b a", "a a a b"},
                {0.9899, "A B A", "A A A B"},
                {0, "", "a a a b"},
                {0, " * * * ", "A A A B"},
                {0.9899, "a a b", "b a a a"},
        };
    }

    @Test
    @UseDataProvider("compareData")
    public void compare(double expected, String a, String b) {
        TextCollection collection = mock(TextCollection.class);
        when(collection.get(eq("a1"))).thenReturn(a);
        when(collection.get(eq("a2"))).thenReturn(b);
        CosineComparator comparator = new CosineComparator(collection, null);
        assertEquals(expected, comparator.compare("a1", "a2"), 0.001);
    }

}