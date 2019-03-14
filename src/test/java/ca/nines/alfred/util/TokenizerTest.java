package ca.nines.alfred.util;


import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class TokenizerTest {

    @Test
    @UseDataProvider("tokenizeData")
    public void tokenize(List<String> expected, String input) throws IOException {
        Tokenizer tokenizer = new Tokenizer();
        assertEquals(expected, tokenizer.tokenize(input));
    }

    @DataProvider()
    public static Object[][] tokenizeData() {
        return new Object[][]{
                {Arrays.asList("cheese", "potatoes"), "cheese potatoes"},
                {Arrays.asList("cheese", "potatoes"), "cheese  potatoes"},
                {Arrays.asList("cheese", "potatoes"), " cheese potatoes"},
                {Arrays.asList("cheese", "potatoes"), "cheese potatoes "},
                {Arrays.asList("cheese,", "potatoes"), "cheese, potatoes "},
                {Arrays.asList("cheese,,", "potatoes"), "cheese,, potatoes "},
                {Arrays.asList("i", "love", "it", "because", "it", "is", "cheese"), "i love it because it is cheese"},
                {Arrays.asList("i", "love", "it's", "cheesey"), "i love it's cheesey"},
        };
    }

    @Test
    @UseDataProvider("tokenizeDataStopWords")
    public void tokenizeWithStopWords(List<String> expected, String input) throws IOException {
        Tokenizer tokenizer = new Tokenizer("nltk");
        assertEquals(expected, tokenizer.tokenize(input));
    }

    @DataProvider()
    public static Object[][] tokenizeDataStopWords() {
        return new Object[][]{
                {Arrays.asList("cheese", "potatoes"), "cheese of potatoes"},
                {Arrays.asList("cheese", "potatoes"), "cheese and potatoes"},
                {Arrays.asList("cheese", "potatoes"), "an cheese potatoes"},
                {Arrays.asList("cheese", "potatoes"), "an cheese and potatoes of the"},
                // The text normalizer will normally remove the comma after and.
                // If it doesn't, this is expected.
                {Arrays.asList("cheese", "and,", "potatoes"), "an cheese and, potatoes of the"},
        };
    }

}
