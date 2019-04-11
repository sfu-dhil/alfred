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
public class WordNGramTokenizerTest {

    @Test
    @UseDataProvider("segmentData")
    public void segment(List<String> expected, String input, int size) {
        Tokenizer ngrams = new WordNGramTokenizer(size);
        assertEquals(expected, ngrams.tokenize(input));
    }

    @DataProvider
    public static Object[][] segmentData() {
        return new Object[][] {
                {Arrays.asList("a b c", "b c d", "c d e"), "a b c d e", 3},
                {Arrays.asList(), "", 3},
                {Arrays.asList(), null, 3},
                {Arrays.asList("a b c d e"), "a b c d e", 6},
        };
    }
}
