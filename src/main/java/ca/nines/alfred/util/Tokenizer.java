package ca.nines.alfred.util;

import ca.nines.alfred.cmd.StopWords;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
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

    public Tokenizer() {
        this(null);
    }

    public Tokenizer(String stopWordFile) {
        logger = LoggerFactory.getLogger(this.getClass());
        stopWords = new HashSet<>();
        if(stopWordFile == null  || stopWordFile.isEmpty()) {
            return;
        }
        InputStream in;
        try {
            in = this.getClass().getResourceAsStream("/" + StopWords.PATH + "/" + stopWordFile);
            if(in == null) {
                throw new FileNotFoundException("Stop word file " + stopWordFile + " was not found.");
            }
            for(String line : IOUtils.readLines(in, StandardCharsets.UTF_8)) {
                if(line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                stopWords.add(line.trim().toLowerCase());
            }
            logger.info("Added {} stopWords from {}.", stopWords.size(), stopWordFile);
        } catch(IOException e) {
            logger.warn("Cannot read stop word file {}. Stop words will not be filtered.", stopWordFile, e);
        }
    }

    public List<String> words(String text) {
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
