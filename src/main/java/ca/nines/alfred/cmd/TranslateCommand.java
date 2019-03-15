/*
 * The MIT License
 *
 * Copyright 2019 Michael Joyce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import com.google.cloud.translate.Translate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

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
