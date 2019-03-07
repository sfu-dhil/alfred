package ca.nines.alfred.wc;

import ca.nines.alfred.cmd.StopWords;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class TokenizingWordCounter extends WordCounter {

    Set<String> stopwords;

    public TokenizingWordCounter() {
        super();
        stopwords = null;
    }

    public TokenizingWordCounter(String stopWordFile) throws IOException {
        super(stopWordFile);
        if(stopWordFile == null) {
            return;
        }
        stopwords = new HashSet<>();
        InputStream in = this.getClass().getResourceAsStream("/" + StopWords.PATH + "/" + stopWordFile);
        for(String line : IOUtils.readLines(in, StandardCharsets.UTF_8)) {
            if(line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            stopwords.add(line.trim().toLowerCase());
        }
    }

    public long count(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        if(stopwords == null) {
            return tokenizer.countTokens();
        }
        long count = 0;
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim().toLowerCase();
            if(stopwords.contains(token)) {
                continue;
            }
            count++;
        }
        return count;
    }

}
