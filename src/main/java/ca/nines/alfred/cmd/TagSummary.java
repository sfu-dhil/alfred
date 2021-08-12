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
import ca.nines.alfred.util.LanguageDecoder;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validate the reports XML.
 */
@CommandInfo(name = "tags", description = "Report tag usage across all reports")
public class TagSummary extends Command {

    private Map<String, List<String>> attrs;

    public TagSummary() {
        super();
        attrs = new HashMap<>();
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for (Report report : corpus) {
            for(Element element : report.getElements()) {
                String name = element.tagName();

                if( ! attrs.containsKey(name)) {
                    attrs.put(name, new ArrayList<>());
                }
                for(Attribute a : element.attributes()) {
                    if (!attrs.get(name).contains(a.getKey())) {
                        attrs.get(name).add(a.getKey());
                    }
                }
            }
        }

        for(String name : attrs.keySet()) {
            System.out.println(name);
            for(String a : attrs.get(name)) {
                System.out.print(" " + a);
            }
            System.out.println();
        }
    }

}
