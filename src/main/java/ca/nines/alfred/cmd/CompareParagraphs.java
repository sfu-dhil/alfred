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
