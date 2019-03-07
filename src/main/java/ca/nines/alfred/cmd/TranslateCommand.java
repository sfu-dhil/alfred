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

@CommandInfo(name = "translate", description = "TranslateCommand documents into English.")
public class TranslateCommand extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption("f", "force", false, "For Retranslations");
        return opts;
    }

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
