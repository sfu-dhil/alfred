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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
            if(report.hasErrors()) {
                sb.append(report.getFile().getName()).append(" has parser errors.\n");
                for(String s : report.getErrors()) {
                    sb.append(s);
                }
            }

            for (String key : required) {
                if (!report.hasMetadata(key)) {
                    sb.append(report.getFile().getName()).append(" is missing required metadata").append("\n");
                    sb.append("  -- " + key + " is missing.\n\n");
                }
            }
            if (report.hasMetadata("dc.date") && !report.getMetadata("dc.date").matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                sb.append(report.getFile().getName()).append(" has invalid dc.date \n");
                sb.append("  -- " + report.getMetadata("dc.date") + " is not parsable.").append("\n\n");
            }
            if (report.hasMetadata("dc.language") && !report.getMetadata("dc.language").matches("^[a-z][a-z]$")) {
                sb.append(report.getFile().getName()).append(" has invalid dc.language ");
                sb.append("  -- " + report.getMetadata("dc.language") + " is not two letters.").append("\n\n");
            }
            if(report.hasMetadata("dc.publisher") && ! report.getTitle().startsWith(report.getMetadata("dc.publisher"))){
                sb.append(report.getFile().getName()).append(" title does not match publisher metadata.\n");
                sb.append("  -- title '" + report.getTitle() + "' does not start with '" + report.getMetadata("dc.publisher") + "'\n\n");
            }
            if(report.hasMetadata("dc.date")) {
                try {
                    LocalDate localDate = LocalDate.parse(report.getMetadata("dc.date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String date = localDate.format(DateTimeFormatter.ofPattern("EEEE, LLLL d, u"));
                    if( ! report.getTitle().endsWith(date)) {
                        sb.append(report.getFile().getName()).append(" title does not match date metadata.\n");
                        sb.append("  -- title '" + report.getTitle() + "' does not end in date '" + date + "'\n\n");
                    }
                } catch(DateTimeParseException e) {
                    // handled above.
                }
            }
            if( ! report.getTitle().contains(" - ")) {
                sb.append(report.getFile().getName()).append(" title is not separated from date properly.\n");
                sb.append("  -- title '" + report.getTitle() + "' does not contain ' - '.\n\n");
            }
            if (report.hasMetadata("dc.source.facsimile")) {
                try {
                    new URL(report.getMetadata("dc.source.facsimile")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    sb.append(report.getFile().getName()).append(" has invalid dc.source.facsimile\n");
                    sb.append("  -- URL is invalid: " + report.getMetadata("dc.source.facsimile")).append("\n\n");
                }
            }
            if (report.hasMetadata("dc.source.url") && !report.getMetadata("dc.source.url").isBlank()) {
                try {
                    new URL(report.getMetadata("dc.source.url")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    sb.append(report.getFile().getName()).append(" has invalid dc.source.url ").append("\n");
                    sb.append("  -- URL is invalid: " + report.getMetadata("dc.source.url")).append("\n\n");
                }
                if( ! report.hasMetadata("dc.source.database")) {
                    sb.append(report.getFile().getName()).append(" has dc.source.url without dc.source.database").append("\n");
                    sb.append("  -- file: " + report.getFile().getPath() + "\n");
                }
            }
            if(report.hasMetadata("dc.source.database") && report.getMetadata("dc.source.database").startsWith("http")) {
                sb.append(report.getFile().getName()).append(" has dc.source.database with a URL").append("\n");
                sb.append("  -- file: " + report.getFile().getPath() + "\n");
            }
            String region = report.getFile().getParentFile().getParentFile().getName();
            if( ! region.equals(report.getMetadata("dc.region"))) {
                sb.append(report.getFile().getName()).append(" may have incorrect dc.region ").append("\n");
                sb.append("  -- Parent folder is " + region + " but dc.region is " + report.getMetadata("dc.region")).append("\n\n");
            }

        }
        System.out.println(sb);
    }

}
