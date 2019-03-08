package ca.nines.alfred.cmd;

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.comparator.CosineComparator;
import ca.nines.alfred.comparator.LevenshteinComparator;
import ca.nines.alfred.comparator.VSMComparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

@CommandInfo(name = "dc", description = "Document comparisons.")
public class CompareDocuments extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addRequiredOption(null, "algorithm", true, "Select algorithm to use. One of lev, cos, vsm.");
        return opts;
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Comparator comparator = null;
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        switch (cmd.getOptionValue("algorithm")) {
            case "lev":
                comparator = new LevenshteinComparator(corpus);
                break;
            case "cos":
                comparator = new CosineComparator(corpus);
                break;
            case "vsm":
                comparator = new VSMComparator(corpus);
                break;
            default:
                throw new Exception("Unknown comparison algorithm " + cmd.getOptionValue("algorithm") + ". Expected one of lev cos vsm.");
        }

        int size = corpus.size();
        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
        for (int i = 0; i < corpus.size(); i++) {
            Report iReport = corpus.get(i);
            for (int j = 0; j < i; j++) {
                Report jReport = corpus.get(j);
                double similarity = comparator.compare(iReport, jReport);
                if(similarity > 0) {
                    iReport.addDocumentSimilarity(new DocumentSimilarity(jReport.getId(), similarity, comparator.getType()));
                    jReport.addDocumentSimilarity(new DocumentSimilarity(iReport.getId(), similarity, comparator.getType()));
                }
                tick();
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
