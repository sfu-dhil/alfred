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

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.ParagraphSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;

/**
 * Compare all the paragraphs in the collection of reports.
 */
@CommandInfo(name = "pc", description = "Paragraph comparisons.")
public class CompareParagraphs extends CompareCommand {

    /**
     * {@inheritDoc}
     * Sets the tick size to 10,000 because the ticks are fast and furious.
     */
    public CompareParagraphs() {
        super();
        tickSize = 10000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        TextCollection collection = corpus.getCollection(true);
        Comparator comparator = getComparator(collection, cmd);

        long size = collection.size();
        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");

        String[] reportIds = corpus.getIds();
        for (int i = 0; i < reportIds.length; i++) {
            Report src = corpus.get(reportIds[i]);
            String[] srcIds = src.getParagraphIds();

            for (int j = 0; j < i; j++) {
                Report dst = corpus.get(reportIds[j]);
                String[] dstIds = dst.getParagraphIds();

                for (String iId : srcIds) {
                    for (String jId : dstIds) {
                        tick();
                        if(src.getParagraph(iId).length() < Comparator.MIN_LENGTH ||
                            dst.getParagraph(jId).length() < Comparator.MIN_LENGTH) {
                            continue;
                        }
                        double similarity = 0;
                        try {
                            similarity = comparator.compare(iId, jId);
                        } catch (IllegalArgumentException e) {
                            logger.error("Cannot compare {} to {}: {}", iId, jId, e.getMessage());
                        }
                        if (similarity <= 0) {
                            continue;
                        }
                        src.addParagraphSimilarity(iId, new ParagraphSimilarity(
                                reportIds[j], jId, similarity, comparator.getType()
                        ));
                        dst.addParagraphSimilarity(jId, new ParagraphSimilarity(
                                reportIds[i], iId, similarity, comparator.getType()
                        ));
                    }
                }
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
