package ca.nines.alfred.tokenizer;

import static org.junit.Assert.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class WordTokenizerTest {

    @Test
    @UseDataProvider("tokenizeData")
    public void normalize(List<String> expected, String input) {
        Tokenizer words = new WordTokenizer();
        assertEquals(expected, words.tokenize(input));
    }

    @DataProvider
    public static Object[][] tokenizeData() {
        return new Object[][]{
                {Arrays.asList("a", "b", "c"), "a b c"},
                {Arrays.asList(), ""},
                {Arrays.asList(), null},
        };
    }
}
