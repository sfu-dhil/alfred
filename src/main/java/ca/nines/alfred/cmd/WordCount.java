package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import ca.nines.alfred.wc.NlpWordCounter;
import ca.nines.alfred.wc.TokenizingWordCounter;
import ca.nines.alfred.wc.WordCounter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.Collection;
import java.util.StringTokenizer;

@CommandInfo(name="wc", description="Count the words in the reports.")
public class WordCount extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption("t", "translations", false, "Also count translations");
        opts.addOption(null, "nlp", false, "Use Stanford CoreNLP to tokenize words");
        opts.addOption(null, "stopwords", true, "Use stopword list.");
        return opts;
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));

        WordCounter counter = new TokenizingWordCounter(cmd.getOptionValue("stopwords"));
        if(cmd.hasOption("nlp")) {
            counter = new NlpWordCounter(cmd.getOptionValue("stopwords"));
        }
        for(Report report : corpus) {
            long count = counter.count(report.getContent());
            report.setMetadata("wr.word-count", "" + count);
            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
