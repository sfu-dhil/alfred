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
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;

/**
 * Compare all the documents in the collection of reports.
 */
@CommandInfo(name = "dc", description = "Document comparisons.")
public class CompareDocuments extends CompareCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        TextCollection collection = corpus.getCollection();
        Comparator comparator = getComparator(collection, cmd);

        long size = collection.size();
        String[] ids = collection.keys();

        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
        for (int i = 0; i < ids.length; i++) {
            for (int j = 0; j < i; j++) {
                double similarity = comparator.compare(ids[i], ids[j]);
                if(similarity > 0) {
                    corpus.get(ids[i]).addDocumentSimilarity(new DocumentSimilarity(ids[j], similarity, comparator.getType()));
                    corpus.get(ids[j]).addDocumentSimilarity(new DocumentSimilarity(ids[i], similarity, comparator.getType()));
                }
                tick();
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
