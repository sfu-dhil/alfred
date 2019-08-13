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
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Translate all the reports using the Google Translate API.
 *
 * Requires the GOOGLE_APPLICATION_CREDENTIALS environment variable to be set. It should be the path to the
 * credentials file downloaded from the Google API Console.
 */
@CommandInfo(name = "translate", description = "TranslateCommand documents into English.")
public class TranslateCommand extends Command {

    /**
     * Adds a -f | --force option, which will retranslate all the translated files.
     * @return configured command line options
     */
    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption("f", "force", false, "For Retranslations");
        return opts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws Exception {
        if (System.getenv("GOOGLE_APPLICATION_CREDENTIALS") == null) {
            throw new Exception("The GOOGLE_APPLICATION_CREDENTIALS environment variable must be set.");
        }
        Translate translator = TranslateOptions.getDefaultInstance().getService();

        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for (Report report : corpus) {
            if (report.getMetadata("dc.language").equals("en")) {
                logger.info("S (en): {}", report.getFile().getName());
                continue;
            }
            if (!cmd.hasOption("force") && report.hasMetadata("wr.translated") && report.getMetadata("wr.translated").equals("yes")) {
                logger.info("S (tr): {}", report.getFile().getName());
                continue;
            }

            logger.info("T ({}): {}", report.getMetadata("dc.language"), report.getFile().getName());
            Translation translation = translator.translate(
                    report.getContentHtml(),
                    TranslateOption.sourceLanguage(report.getMetadata("dc.language")),
                    TranslateOption.targetLanguage("en"),
                    TranslateOption.format("html"));

            report.setTranslation(translation.getTranslatedText());
            Thread.sleep(1000); // sleep for one second.
        }
        CorpusWriter.write(corpus);
    }
}
