package ca.nines.alfred.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class TextTest {

    @Test
    @UseDataProvider("normalizeData")
    public void normalize(String expected, String input) {
        assertEquals(expected, Text.normalize(input));
    }

    @DataProvider
    public static Object[][] normalizeData() {
        return new String[][]{
                {"cheese", "  cheese"},
                {"cheese", "cheese  "},
                {"cheese", "cheese!"},
                {"cheese", "CHEESE!!"},
                {"cheese cheese", "cheese  cheese"},
                {"cheesecheese", "cheese!!!cheese"},
                {"", null},
                {"", ""}
        };
    }
}