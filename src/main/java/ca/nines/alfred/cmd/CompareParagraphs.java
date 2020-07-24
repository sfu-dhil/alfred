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
import ca.nines.alfred.comparator.CosineComparator;
import ca.nines.alfred.comparator.ExactComparator;
import ca.nines.alfred.comparator.LevenshteinComparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.ParagraphSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import ca.nines.alfred.tokenizer.Tokenizer;
import ca.nines.alfred.tokenizer.WordTokenizer;
import ca.nines.alfred.util.Text;
import org.apache.commons.cli.CommandLine;

/**
 * Compare all the paragraphs in the collection of reports.
 */
@CommandInfo(name = "pc", description = "Paragraph comparisons.")
public class CompareParagraphs extends Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        Tokenizer words = new WordTokenizer();

        Comparator lev = new LevenshteinComparator();

        for(Report report : corpus) {
            for(String id : report.getParagraphIds(false)) {
                lev.add(id, Text.normalize(report.getParagraph(id)));
            }
        }
        lev.complete();

        long size = corpus.size();
        long comparisons = lev.size() * (lev.size() - 1) / 2;
        tickSize = 10000;
        out.println("Expect " + formatter.format(comparisons) + " comparisons.");
        String[] ids = corpus.getIds();

        for(int i = 0; i < size; i++) {
            String srcId = ids[i];
            Report srcReport = corpus.get(srcId);
            for (int j = 0; j < i; j++) {

                String dstId = ids[j];
                Report dstReport = corpus.get(dstId);

                if( ! srcReport.getLanguage().equals(dstReport.getLanguage())) {
                    continue;
                }

                for(String m : srcReport.getParagraphIds()) {
                    for(String n : dstReport.getParagraphIds()) {
                        tick();
                        double ls = lev.compare(m, n);
                        if(ls > 0) {
                            srcReport.addParagraphSimilarity(m, new ParagraphSimilarity(dstId, n, ls, "lev"));
                            dstReport.addParagraphSimilarity(n, new ParagraphSimilarity(srcId, m, ls, "lev"));
                        }
                    }
                }
            }
        }

        reset();
        CorpusWriter.write(corpus);
    }
}
