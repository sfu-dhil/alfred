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

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;

/**
 * Compare all the paragraphs in the collection of reports.
 */
@CommandInfo(name = "pc", description = "Paragraph comparisons.")
public class CompareParagraphs extends Command {

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

        long size = corpus.size();
        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
    }
}
