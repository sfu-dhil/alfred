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
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validate the reports XML.
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

    protected void log(Report report, String s, String... detail) {
        System.out.println(s);
        for(String d : detail) {
            System.out.println("  " + d);
        }
        System.out.println("  -- file: " + report.getFile().getPath() + "\n");
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
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        for (Report report : corpus) {
            String p = report.getFile().getPath();
            for(String s : p.split(File.separator)) {
                if(s.startsWith(" ") || s.endsWith(" ")) {
                    log(report, "Folder or file name that starts or ends with a space");
                }
            }

            if(report.hasErrors()) {
                log(report, "XML parser errors", report.getErrors().toArray(new String[0]));
            }

            for(String e : report.checkStructure()) {
                log(report, "Structural problems", e);
            }

            for (String key : required) {
                if (!report.hasMetadata(key)) {
                    log(report, "Missing required metadata", key + " is missing.");
                }
            }

            if (report.hasMetadata("dc.language") && !report.getMetadata("dc.language").matches("^[a-z][a-z]$")) {
                log(report, "Invalid dc.language", report.getMetadata("dc.language") + " is not two letters.");
            }

            if(report.hasMetadata("dc.publisher") && ! report.getTitle().startsWith(report.getMetadata("dc.publisher"))){
                log(report, "Title does not match publisher metadata", "title '" + report.getTitle() + "' does not start with " + report.getMetadata("dc.publisher"));
            }

            if (report.hasMetadata("dc.date") && !report.getMetadata("dc.date").matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                log(report, "Invalid dc.date", report.getMetadata("dc.date") + " is not parsable.");
            }

            if(report.hasMetadata("dc.date")) {
                try {
                    LocalDate localDate = LocalDate.parse(report.getMetadata("dc.date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String date = localDate.format(DateTimeFormatter.ofPattern("EEEE, LLLL d, u"));

                    if( ! report.getTitle().endsWith(date)) {
                        log(report, "Title does not match date metadata", "title '" + report.getTitle() + "' does not end in date '" + date);
                    }
                } catch(DateTimeParseException e) {
                    log(report, "Cannot parse dc.date", report.getMetadata("dc.date") + " is not formatted YYYY-MM-DD");
                }
            }

            if( ! report.getTitle().contains(" - ")) {
                log(report, "title does not contain a hyphen and is not separated from date properly.");
            }

            if (report.hasMetadata("dc.source.facsimile")) {
                try {
                    new URL(report.getMetadata("dc.source.facsimile")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    log(report, "Invalid dc.source.facsimile URL", report.getMetadata("dc.source.facsimile") + " cannot be parsed", e.getMessage());
                }
            }

            if (report.hasMetadata("dc.source.url") && !report.getMetadata("dc.source.url").isBlank()) {
                try {
                    new URL(report.getMetadata("dc.source.url")).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    log(report, "Invalid dc.source.url", report.getMetadata("dc.source.url") + " cannot be parsed", e.getMessage());
                }
            }

            if(report.hasMetadata("dc.source.database") && report.getMetadata("dc.source.database").startsWith("http")) {
                log(report, "dc.source.database looks like a URL");
            }

            try {
                String region = report.getFile().getParentFile().getParentFile().getName();
                if( ! region.equals(report.getMetadata("dc.region"))) {
                    log(report, "Possibly incorrect dc.region", "Parent folder is " + region + " but dc.region is " + report.getMetadata("dc.region"));
                }
            } catch (Exception e) {
                log(report, "Possibly misfiled.");
            }

        }
    }

}
