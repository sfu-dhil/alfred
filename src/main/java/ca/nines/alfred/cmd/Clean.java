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
import org.apache.commons.cli.Options;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name="clean", description="Clean the files in a directory.")
public class Clean extends Command {

    /**
     * Add options to the command line parser.
     *
     *  -t | --translations will remove the translations from the reports.
     *       --ids will remove IDs and generate them fresh.
     *
     * @return configured options.
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption("t", "translations", false, "Also remove translations");
        opts.addOption(null, "ids", false, "Also reset IDs on documents and paragraphs.");
        return opts;
    }

    /**
     * Read all the XML documents in one or more directories and clean them. Writes the cleaned XML back to the
     * file.
     *
     * @param cmd Parsed command line.
     * @throws Exception for IO errors.
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        int n = 0;
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for(Report report : corpus) {
            n++;
            String id = report.getFile().getName().replaceAll("[^A-Z]", "").toLowerCase() + "_" + n;

            if( ! report.hasId() || cmd.hasOption("ids")) {
                report.setId(id);
                report.setParagraphIds();
            }
            if(cmd.hasOption("translations")) {
                report.setTranslation(null);
            }
            report.removeDocumentSimilarities();
            report.removeParagraphSimilarities();
            report.setMetadata("wr.path", null);
            report.setMetadata("wr.word-count", null);
            report.setMetadata("wr.wordcount", null);
            report.setMetadata("index.paragraph", null);
            report.setMetadata("index.document", null);

            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
