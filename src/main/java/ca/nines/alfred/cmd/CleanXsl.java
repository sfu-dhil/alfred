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
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.s9api.*;
import net.sf.saxon.serialize.SerializationProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URI;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name = "cleanxsl", description = "Clean the files in a directory.")
public class CleanXsl extends Command {

    private static final String URL = "https://raw.githubusercontent.com/sfu-dhil/wilde-schema/main/schema/clean.xsl";

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "url", true, "Use XSL at URL.");
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
        String url = URL;
        if (cmd.hasOption("url")) {
            url = cmd.getOptionValue("url");
        }

        Source xslt = new StreamSource(url);
        Processor processor = new Processor(false);
        XsltCompiler compiler = processor.newXsltCompiler();
        XsltExecutable executable = compiler.compile(xslt);
        XsltTransformer transformer = executable.load();

        String root = getArgList(cmd)[0];
        for(File file : FileUtils.listFiles(new File(root), new String[]{"xml"}, true)) {
            Serializer serializer = processor.newSerializer(file);
            Source xml = new StreamSource(file);
            transformer.setSource(xml);
            transformer.setDestination(serializer);
            transformer.transform();
            tick();
        }
        reset();
    }
}
