package ca.nines.alfred.util;

import ca.nines.alfred.cmd.StopWords;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Tokenizer {

    private final Set<String> stopWords;

    protected final Logger logger;

    private final int nGramSize;

    public Tokenizer()  throws IOException{
        this(null, 1);
    }

    public Tokenizer(String stopWordsFile)  throws IOException{
        this(stopWordsFile, 1);
    }

    public Tokenizer(int nGramSize)  throws IOException{
        this(null, nGramSize);
    }

    public Tokenizer(String stopWordsFile, int nGramSize) throws IOException {
        this.nGramSize = nGramSize;
        stopWords = new HashSet<>();
        logger = LoggerFactory.getLogger(this.getClass());
        if(stopWordsFile == null) {
            return;
        }

        InputStream in = getClass().getResourceAsStream("/" + StopWords.PATH + "/" + stopWordsFile);
        for(String line : IOUtils.readLines(in, StandardCharsets.UTF_8)) {
            if(line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            stopWords.add(line.trim().toLowerCase());
        }
        logger.info("Added {} stopWords from {}.", stopWords.size(), stopWordsFile);
    }

    public List<String> tokenize(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        List<String> words = new ArrayList<>();
        while(tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().trim().toLowerCase();
            if(stopWords != null && stopWords.contains(word)) {
                continue;
            }
            words.add(word);
        }
        return words;
    }

}
