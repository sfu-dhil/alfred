package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.vsm.VectorSpaceModel;
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
public class VSMComparatorTest {

    @Test
    @UseDataProvider("compareData")
    public void compare(double expected, double result ) {
        VectorSpaceModel vsm = mock(VectorSpaceModel.class);
        when(vsm.compare(eq("a1"), eq("a2"))).thenReturn(result);
        VSMComparator comparator = new VSMComparator(vsm);
        assertEquals(expected, comparator.compare("a1", "a2"), 0.001);
    }

    @DataProvider
    public static Object[][] compareData() {
        return new Object[][] {
                {0.0, 0.0},
                {0.0, -1.0},
                {0.0, 0.5},
                {0.9, 0.9},
                {1.0, 1.00001},
        };
    }

}