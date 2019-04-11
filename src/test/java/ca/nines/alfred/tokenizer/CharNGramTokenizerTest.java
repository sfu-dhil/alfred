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
public class CharNGramTokenizerTest {

    @Test
    @UseDataProvider("ngramsData")
    public void ngrams(List<String> expected, String input, int size) {
        Tokenizer ngram = new CharNGramTokenizer(size);
        assertEquals(expected, ngram.tokenize(input));
    }

    @DataProvider
    public static Object[][] ngramsData() {
        return new Object[][]{
                {Arrays.asList("imp", "mpo", "por", "ort"), "import", 3},
                {Arrays.asList("green", "reen ", "een h", "en ho", "n hou", " hous", "house"), "green house", 5},
                {Arrays.asList("cat"), "cat", 3},
                {Arrays.asList("cat"), "cat", 5},
                {Arrays.asList(), "", 5},
                {Arrays.asList(), null, 5},
        };
    }

}
