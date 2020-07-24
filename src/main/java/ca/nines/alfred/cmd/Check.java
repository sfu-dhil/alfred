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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name = "check", description = "Check the metadata in the files in a directory.")
public class Check extends Command {

    static String[] required = {
            "dc.language",
            "dc.date",
            "dc.publisher",
            "dc.region",
            "dc.region.city",
    };

    /**
     * Read all the XML documents in one or more directories and clean them. Writes the cleaned XML back to the
     * file.
     *
     * @param cmd Parsed command line.
     * @throws Exception for IO errors.
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        StringBuilder sb = new StringBuilder();
        for (Report report : corpus) {
            for (String key : required) {
                if (!report.hasMetadata(key)) {
                    sb.append(report.getFile().getName()).append(" is missing ").append(key).append("\n");
                }
            }
            if (report.hasMetadata("dc.date") && !report.getMetadata("dc.date").matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                sb.append(report.getFile().getName()).append(" has invalid dc.date ").append(report.getMetadata("dc.date")).append("\n");
            }
            if (report.hasMetadata("dc.language") && !report.getMetadata("dc.language").matches("^[a-z][a-z]$")) {
                sb.append(report.getFile().getName()).append(" has invalid dc.language ").append(report.getMetadata("dc.language")).append("\n");
            }
            if (report.hasMetadata("dc.source.facsimile")) {
                try {
                    new URL(report.getMetadata("dc.source.facsimile")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    sb.append(report.getFile().getName()).append(" has invalid dc.source.facsimile ").append(report.getMetadata("dc.source.facsimile")).append("\n");
                }
            }
            if (report.hasMetadata("dc.source.url") && !report.getMetadata("dc.source.url").isBlank()) {
                try {
                    new URL(report.getMetadata("dc.source.url")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    sb.append(report.getFile().getName()).append(" has invalid dc.source.url ").append(report.getMetadata("dc.source.url")).append("\n");
                }
            }
        }
        System.out.println(sb);
    }

}
