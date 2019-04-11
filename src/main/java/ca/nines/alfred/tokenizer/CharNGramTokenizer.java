package ca.nines.alfred.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class CharNGramTokenizer implements Tokenizer {

    private final int size;

    public CharNGramTokenizer(int size) {
        this.size = size;
    }

    @Override
    public List<String> tokenize(String text) {
        List<String> segments = new ArrayList<>();
        if(text == null || text.equals("")) {
            return segments;
        }
        if(text.length() <= size) {
            segments.add(text);
            return segments;
        }

        for(int i = 0; i <= text.length() - size; i++) {
            segments.add(text.substring(i, i+size));
        }

        return segments;
    }

}
