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
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Exports the files in a directory
 */
@CommandInfo(name = "export", description = "Export files to a directory.")
public class Export extends Command {

    /**
     * Add options to the command line parser.
     *
     *  -d | --directory Directory to write the text files in.
     *
     * @return configured options.
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addRequiredOption(null, "directory", true, "Directory for export");
        opts.addOption(null, "text", false, "Export as text");
        return opts;
    }

    /**
     * Read all the XML documents in one or more directories and export them.
     *
     * @param cmd Parsed command line.
     * @throws Exception for IO errors.
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        String directory = cmd.getOptionValue("directory");
        boolean text = cmd.hasOption("text");
        for (Report report : corpus) {
            String filename = report.getFile().getName();
            if(text) {
                filename = filename.replace(".xml", ".txt");
            }
            File file = new File(directory + "/" + report.getMetadata("dc.region") + "/" + report.getMetadata("dc.publisher") + "/" + filename);
            String content;
            if(text) {
                content = report.getContent(false);
            } else {
                content = report.serialize();
            }
            FileUtils.writeStringToFile(file, content, "UTF-8");
            tick();
        }
        reset();
    }
}
