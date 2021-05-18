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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Clean removes added metadata from the reports XML.
 */
@CommandInfo(name="pidx", description="Assign IDs to publishers and use the IDs in links.")
public class PublisherIndex extends Command {

    public PublisherIndex() {
        super();
    }

    /**
     * Add options to the command line parser.
     *
     *  -f | --file file
     *      File to write the index
     *
     * @return configured options.
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addRequiredOption("f", "file", true, "File to write the index");
        return opts;
    }


    /**
     * Read all the XML documents in one or more directories and figure out the paper IDs.
     *
     * @param cmd Parsed command line.
     * @throws Exception for IO errors.
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        String file = cmd.getOptionValue("file");

        Corpus corpus = CorpusReader.read(getArgList(cmd));
        Map<String, String> ids = new HashMap<>();

        logger.info("Generating publisher index");
        for(Report report : corpus) {
            ids.put(report.getMetadata("dc.publisher.id"), report.getMetadata("dc.publisher"));
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element index = doc.createElement("index");
        doc.appendChild(index);
        for(String id : ids.keySet()) {
            Element item = doc.createElement("item");
            item.setAttribute("id", id);
            item.setAttribute("name", ids.get(id));
            index.appendChild(item);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(file));
        transformer.transform(source, result);
    }

}
