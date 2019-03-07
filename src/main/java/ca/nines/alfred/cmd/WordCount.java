package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import ca.nines.alfred.util.Tokenizer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

@CommandInfo(name="wc", description="Count the words in the reports.")
public class WordCount extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "stopwords", true, "Use stopword list.");
        return opts;
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        Tokenizer tokenizer;
        if(cmd.hasOption("stopwords")) {
            tokenizer = new Tokenizer(cmd.getOptionValue("stopwords"));
        } else {
            tokenizer = new Tokenizer();
        }
        for(Report report : corpus) {
            long count = tokenizer.words(report.getContent()).size();
            report.setMetadata("wr.word-count", "" + count);
            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
