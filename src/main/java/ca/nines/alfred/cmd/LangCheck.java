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
import com.google.api.client.util.ArrayMap;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Validate the reports XML.
 */
@CommandInfo(name = "langcheck", description = "Check the metadata in the files in a directory.")
public class LangCheck extends Command {

    private final String MODEL="data/langdetect-183.bin";

    private final double THRESHOLD = 0.15;

    protected void log(Report report, String s, String... detail) {
        System.out.println(s);
        for(String d : detail) {
            System.out.println("  " + d);
        }
        System.out.println("  -- file: " + report.getFile().getPath() + "\n");
    }

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "model", true, "Language model. Defaults to " + MODEL);
        opts.addOption(null, "threshold", true, "Threshold for reporting. Defaults to " + THRESHOLD);
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
        String modelPath = cmd.getOptionValue("model", MODEL);
        double threshold = Double.parseDouble(cmd.getOptionValue("threshold", "" + THRESHOLD));
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        InputStream is = new FileInputStream(modelPath);
        LanguageDetectorModel ldm = new LanguageDetectorModel(is);
        LanguageDetector detector = new LanguageDetectorME(ldm);

        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMinimumFractionDigits(1);

        for (Report report : corpus) {
            String declared = LanguageDecoder.codeToLanguage(report.getLanguage());
            if(declared == null) {
//                log(report, "Cannot find declared language.");
                continue;
            }
            if(report.getContent(true) == null) {
//                log(report, "Cannot find original content to check language.");
                continue;
            }
            Language best = detector.predictLanguage(report.getContent(true));

            if(declared.equals(LanguageDecoder.codeToLanguage(best.getLang()))) {
                continue;
            }
            if(best.getConfidence() < threshold) {
                continue;
            }

            log(report, "Declared language " + report.getLanguage() + " may be wrong.",
                    "Seems like language " + best.getLang().substring(0,2) + " with confidence " + fmt.format(best.getConfidence()));
        }
    }

}
