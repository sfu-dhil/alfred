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
import ca.nines.alfred.util.LanguageDecoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Exports the files in a directory
 */
@CommandInfo(name = "data", description = "Analyze the reports and export some data")
public class Data extends Command {

    /**
     * Add options to the command line parser.
     * <p>
     * -d | --directory Directory to write the csv files in.
     *
     * @return configured options.
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addRequiredOption(null, "directory", true, "Directory for export");
        return opts;
    }

    public void write(Path path, String[] headers, List<String[]> data) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            BufferedWriter writer = Files.newBufferedWriter(path);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            printer.printRecords(data);
            printer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void volume(Path path, Corpus corpus) {
        String[] headers = new String[]{"ID", "Date", "Word count", "Document matches", "Paragraph matches", "Newspaper title", "Region", "City", "Language"};
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            String[] row = new String[]{
                    report.getId(),
                    report.getMetadata("dc.date"),
                    report.getMetadata("wr.word-count"),
                    String.valueOf(report.getDocumentSimilarities().size()),
                    String.valueOf(report.getParagraphSimilarities().size()),
                    report.getMetadata("dc.publisher"),
                    report.getMetadata("dc.region"),
                    report.getMetadata("dc.region.city"),
                    LanguageDecoder.codeToLanguage(report.getMetadata("dc.language")),
            };
            data.add(row);
        }
        write(Paths.get(path + "/volume.csv"), headers, data);
    }

    public void documentMatches(Path path, Corpus corpus) {
        String[] headers = new String[]{
                "Report Id", "Report date", "Report paper", "Report region", "Report city",
                "Match Id", "Match date", "Match region", "Match city", "Match similarity"};
        Set<String> seen = new HashSet<>();
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            for (DocumentSimilarity similarity : report.getDocumentSimilarities()) {
                Report match = corpus.get(similarity.getReportId());
                if (seen.contains(match.getId())) {
                    continue;
                }
                String[] row = new String[]{
                        report.getId(),
                        report.getMetadata("dc.date"),
                        report.getMetadata("dc.publisher"),
                        report.getMetadata("dc.region"),
                        report.getMetadata("dc.region.city"),
                        match.getId(),
                        match.getMetadata("dc.date"),
                        match.getMetadata("dc.publisher"),
                        match.getMetadata("dc.region"),
                        match.getMetadata("dc.region.city"),
                        String.valueOf(similarity.getSimilarity()),
                };
                data.add(row);
            }
            seen.add(report.getId());
        }
        write(Paths.get(path + "/document-matches.csv"), headers, data);
    }

    public void paragraphMatches(Path path, Corpus corpus) {
        String[] headers = new String[]{
                "Report Id", "Paragraph Id", "Report date", "Report paper", "Report region", "Report city",
                "Match Id", "Match Paragraph Id", "Match date", "Match region", "Match city", "Match similarity"};
        Set<String> seen = new HashSet<>();
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            for (String pid : report.getParagraphIds()) {
                for (ParagraphSimilarity similarity : report.getParagraphSimilarities(pid)) {
                    if(seen.contains(similarity.getParagraphId())) {
                        continue;
                    }
                    Report match = corpus.get(similarity.getReportId());
                    String[] row = new String[]{
                            report.getId(),
                            pid,
                            report.getMetadata("dc.date"),
                            report.getMetadata("dc.publisher"),
                            report.getMetadata("dc.region"),
                            report.getMetadata("dc.region.city"),
                            match.getId(),
                            similarity.getParagraphId(),
                            match.getMetadata("dc.date"),
                            match.getMetadata("dc.publisher"),
                            match.getMetadata("dc.region"),
                            match.getMetadata("dc.region.city"),
                            String.valueOf(similarity.getSimilarity()),
                    };
                    data.add(row);
                }
                seen.add(pid);
            }
        }
        write(Paths.get(path + "/paragraph-matches.csv"), headers, data);
    }

    public void signatures(Path path, Corpus corpus) {
        String[] headers = new String[]{"ID", "Date", "Newspaper title", "Region", "City", "Language", "Signature"};
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            String sig = report.getSignature();
            if(sig == null) {
                continue;
            }
            String[] row = new String[]{
                    report.getId(),
                    report.getMetadata("dc.date"),
                    report.getMetadata("dc.publisher"),
                    report.getMetadata("dc.region"),
                    report.getMetadata("dc.region.city"),
                    LanguageDecoder.codeToLanguage(report.getMetadata("dc.language")),
                    sig,
            };
            data.add(row);
        }
        write(Paths.get(path + "/signatures.csv"), headers, data);
    }

    public void bibliography(Path path, Corpus corpus) {
        String[] headers = new String[]{"ID", "Date", "Newspaper title", "Region", "City", "Language"};
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            String[] row = new String[]{
                    report.getId(),
                    report.getMetadata("dc.date"),
                    report.getMetadata("dc.publisher"),
                    report.getMetadata("dc.region"),
                    report.getMetadata("dc.region.city"),
                    LanguageDecoder.codeToLanguage(report.getMetadata("dc.language")),
            };
            data.add(row);
        }
        write(Paths.get(path + "/bibliography.csv"), headers, data);
    }

    public void documentNetworkNodes(Path path, Corpus corpus) {
        String[] headers = new String[]{"ID", "Label", "Language", "Region", "City"};
        List<String[]> data = new ArrayList<>();
        for (Report report : corpus) {
            if(report.getDocumentSimilarities().size() == 0) {
                continue;
            }
            String[] row = new String[]{
                    report.getId(),
                    report.getMetadata("dc.publisher"),
                    LanguageDecoder.codeToLanguage(report.getMetadata("dc.language")),
                    report.getMetadata("dc.region"),
                    report.getMetadata("dc.region.city"),
            };
            data.add(row);
        }
        write(Paths.get(path + "/gephi-document-nodes.csv"), headers, data);
    }

    public void documentNetworkEdges(Path path, Corpus corpus) {
        String[] headers = new String[]{"source", "target", "type", "weight"};
        List<String[]> data = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Report report : corpus) {
            for(DocumentSimilarity similarity : report.getDocumentSimilarities()) {
                if(seen.contains(similarity.getReportId())) {
                    continue;
                }
                String[] row = new String[]{
                        report.getId(),
                        similarity.getReportId(),
                        "undirected",
                        String.valueOf(similarity.getSimilarity()),
                };
                data.add(row);
            }
            seen.add(report.getId());
        }
        write(Paths.get(path + "/gephi-document-matches.csv"), headers, data);
    }

    public void publisherNetworkNodes(Path path, Corpus corpus) {
        String[] headers = new String[]{"ID", "Label", "Language", "Region", "City"};
        List<String[]> data = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Report report : corpus) {
            if(seen.contains(report.getMetadata("dc.publisher.id"))) {
                continue;
            }
            seen.add(report.getMetadata("dc.publisher.id"));

            String[] row = new String[]{
                    report.getMetadata("dc.publisher.id"),
                    report.getMetadata("dc.publisher"),
                    LanguageDecoder.codeToLanguage(report.getMetadata("dc.language")),
                    report.getMetadata("dc.region"),
                    report.getMetadata("dc.region.city"),
            };
            data.add(row);
        }
        write(Paths.get(path + "/gephi-newspaper-nodes.csv"), headers, data);
    }

    public void publisherNetworkEdges(Path path, Corpus corpus) {
        String[] headers = new String[]{"source", "target", "type", "weight"};
        Map<Pair<String,String>,Integer> count = new HashMap<>();
        for (Report report : corpus) {
            String srcId = report.getMetadata("dc.publisher.id");
            for(DocumentSimilarity similarity : report.getDocumentSimilarities()) {
                String dstId = corpus.get(similarity.getReportId()).getMetadata("dc.publisher.id");
                if(srcId.compareTo(dstId) < 0) {
                    continue;
                }
                Pair<String,String> p = new ImmutablePair<>(srcId, dstId);
                if( ! count.containsKey(p)) {
                    count.put(p, 1);
                } else {
                    count.put(p, count.get(p)+1);
                }
            }
        }
        List<String[]> data = new ArrayList<>();
        for(Pair<String,String> p : count.keySet()) {
            String row[] = {
                    p.getLeft(),
                    p.getRight(),
                    "undirected",
                    String.valueOf(count.get(p)),
            };
            data.add(row);
        }
        write(Paths.get(path + "/gephi-newspaper-edges.csv"), headers, data);
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
        Path path = Paths.get(directory);
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        volume(path, corpus);
        documentMatches(path, corpus);
        paragraphMatches(path, corpus);
        signatures(path, corpus);
        bibliography(path, corpus);
        documentNetworkNodes(path, corpus);
        documentNetworkEdges(path, corpus);
        publisherNetworkNodes(path, corpus);
        publisherNetworkEdges(path, corpus);
    }
}
