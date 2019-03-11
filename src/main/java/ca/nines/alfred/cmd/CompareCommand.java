package ca.nines.alfred.cmd;

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.comparator.CosineComparator;
import ca.nines.alfred.comparator.LevenshteinComparator;
import ca.nines.alfred.comparator.VSMComparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.io.CorpusReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

abstract public class CompareCommand extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "stopwords", true, "Use stopword list.");
        opts.addRequiredOption(null, "algorithm", true, "Select algorithm to use. One of lev, cos, vsm.");
        return opts;
    }

    protected Comparator getComparator(Corpus corpus, CommandLine cmd) throws Exception {
        String stopWordsFile = cmd.getOptionValue("stopwords");
        String algorithm = cmd.getOptionValue("algorithm");
        switch (algorithm) {
            case "lev":
                return new LevenshteinComparator(corpus, stopWordsFile);
            case "cos":
                return new CosineComparator(corpus, stopWordsFile);
            case "vsm":
                return new VSMComparator(corpus, stopWordsFile);
            default:
                throw new Exception("Unknown comparison algorithm " + algorithm + ". Expected one of lev cos vsm.");
        }
    }

}
