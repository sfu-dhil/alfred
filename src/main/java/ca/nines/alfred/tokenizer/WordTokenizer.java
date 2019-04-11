package ca.nines.alfred.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        if(text == null || text.equals("")) {
            return new ArrayList<>();
        }
        return Arrays.asList(text.split("\\s"));
    }
}
