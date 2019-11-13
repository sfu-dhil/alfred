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
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.ParagraphSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name="pid", description="Assign IDs to publishers and use the IDs in links.")
public class AssignPaperIds extends Command {

    private final Map<String, String> ids;

    public AssignPaperIds() {
        super();
        ids = new HashMap<>();
    }

    private String getId(Report report) {
        String region = report.getMetadata("dc.region");
        String publisher = report.getMetadata("dc.publisher");
        String key = region + "::" + publisher;
        if( ! ids.containsKey(key)) {
            String id = region.charAt(0) + "_";
            String[] parts = publisher.split("[^a-zA-Z]+");
            for(String part : parts) {
                id += part.charAt(0);
            }
            id += "_" + (ids.keySet().size() + 1);
            ids.put(key, id.toLowerCase());
        }
        return ids.get(key);
    }

    /**
     * Read all the XML documents in one or more directories and figure out the paper IDs.
     *
     * @param cmd Parsed command line.
     * @throws Exception for IO errors.
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        logger.info("Generating IDs");
        for(Report report : corpus) {
            String id = getId(report);
            report.setMetadata("dc.publisher.id", id);
        }

        logger.info("Adding IDs to link elements");
        for(Report report : corpus) {
            for(DocumentSimilarity ds : report.getDocumentSimilarities()) {
                Report target = corpus.get(ds.getReportId());
                ds.setPaperId(getId(target));
            }
        }

        logger.info("Adding IDs to a elements");
        for(Report report : corpus) {
            for(ParagraphSimilarity ps : report.getParagraphSimilarities()) {
                Report target = corpus.get(ps.getReportId());
                ps.setPaperId(getId(target));
            }
        }

        CorpusWriter.write(corpus);
    }

}
