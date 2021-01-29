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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name="articles", description="Generate sortable names by removing initial articles")
public class Articles extends Command {

    private Map<String,String[]> getArticles() {
        Map<String,String[]> articles = new HashMap<>();

        Properties data = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/articles.properties");
        try {
            data.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(String lang : data.stringPropertyNames()) {
            articles.put(lang, data.getProperty(lang).split(","));
        }

        return articles;
    }

    private void setSortableTitle(Report report, Map<String, String[]> articles) {
        String title = report.getTitle().toLowerCase();
        String publisher = report.getMetadata("dc.publisher").toLowerCase();
        String lang = report.getLanguage();
        if(articles.containsKey(lang)) {
            for(String a : articles.get(lang)) {
                if(title.startsWith(a)) {
                    title = title.substring(a.length());
                    break;
                }
            }
            for(String a : articles.get(lang)) {
                if(publisher.startsWith(a)) {
                    publisher = publisher.substring(a.length()).trim();
                    break;
                }
            }
        }

        if(!title.contains(" - ")) {
            logger.warn("'" + report.getFile().getPath() + "' has malformed title and may be sorted incorrectly.");
            logger.warn(title);
        } else {
            title = title.substring(0, title.indexOf(" - ")).trim() + " - " + report.getMetadata("dc.date");
        }

        report.setMetadata("wr.sortable", title);
        report.setMetadata("dc.publisher.sortable", publisher);
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
        Map<String, String[]> articles = getArticles();
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for(Report report : corpus) {
            setSortableTitle(report, articles);
            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
