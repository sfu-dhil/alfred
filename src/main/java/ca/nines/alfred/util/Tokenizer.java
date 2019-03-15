/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

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
