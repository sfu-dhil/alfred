/*
 * The MIT License
 *
 * Copyright 2019 Michael Joyce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import ca.nines.alfred.util.Tokenizer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Count the words in each document and set the relevant metadata for each file.
 */
@CommandInfo(name="wc", description="Count the tokenize in the reports.")
public class WordCount extends Command {

    /**
     * Adds a --stopwords option to use one of the stop words files to file out common words.
     *
     * @return options for the command line
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "stopwords", true, "Use stopword list.");
        return opts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        Tokenizer tokenizer;
        if(cmd.hasOption("stopwords")) {
            tokenizer = new Tokenizer(cmd.getOptionValue("stopwords"));
        } else {
            tokenizer = new Tokenizer();
        }
        for(Report report : corpus) {
            long count = tokenizer.tokenize(report.getContent()).size();
            report.setMetadata("wr.word-count", "" + count);
            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
