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
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import ca.nines.alfred.tokenizer.Tokenizer;
import ca.nines.alfred.tokenizer.WordTokenizer;
import ca.nines.alfred.util.Text;
import org.apache.commons.cli.CommandLine;

/**
 * Compare all the documents in the collection of reports.
 */
@CommandInfo(name = "dc", description = "Document comparisons.")
public class CompareDocuments extends Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        String[] ids = corpus.getIds();
        long size = corpus.size();

        Tokenizer words = new WordTokenizer();

        Comparator ext = new ExactComparator();
        Comparator lev = new LevenshteinComparator();
        Comparator cos = new CosineComparator(words);

        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");

        for(Report report : corpus) {
            ext.add(report.getId(), Text.normalize(report.getContent()));
            lev.add(report.getId(), Text.normalize(report.getContent()));
            cos.add(report.getId(), Text.normalize(report.getContent()));
        }

        for(int i = 0; i < size; i++) {
            String srcId = ids[i];
            Report srcReport = corpus.get(srcId);
            for(int j = 0; j < i; j++) {
                tick();
                String dstId = ids[j];
                Report dstReport = corpus.get(dstId);

                if( ! srcReport.getLanguage().equals(dstReport.getLanguage())) {
                    continue;
                }

                double ls = lev.compare(srcId, dstId);
                if(ext.compare(srcId, dstId) > 0) {
                    srcReport.addDocumentSimilarity(new DocumentSimilarity(dstId, 1.0, "exact"));
                    dstReport.addDocumentSimilarity(new DocumentSimilarity(srcId, 1.0, "exact"));
                    continue;
                }
                if(ls > 0) {
                    srcReport.addDocumentSimilarity(new DocumentSimilarity(dstId, ls, "lev"));
                    dstReport.addDocumentSimilarity(new DocumentSimilarity(srcId, ls, "lev"));
                }
                double cs = cos.compare(srcId, dstId);
                if(cs > 0) {
                    srcReport.addDocumentSimilarity(new DocumentSimilarity(dstId, cs, "cos"));
                    dstReport.addDocumentSimilarity(new DocumentSimilarity(srcId, cs, "cos"));
                }
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
