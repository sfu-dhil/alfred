package ca.nines.alfred.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class WordNGramTokenizer implements Tokenizer {

    private final int size;

    public WordNGramTokenizer(int size) {
        this.size = size;
    }

    @Override
    public List<String> tokenize(String text) {
        List<String> segments = new ArrayList<>();
        if(text == null || text.equals("")) {
            return segments;
        }

        WordTokenizer s = new WordTokenizer();
        List<String> words = s.tokenize(text);

        if(words.size() < size) {
            segments.add(String.join(" ", words));
            return segments;
        }

        for(int i = 0; i <= words.size() - size; i++) {
            List<String> ngram = words.subList(i, i+size);
            segments.add(String.join(" ", ngram));
        }

        return segments;
    }

}
