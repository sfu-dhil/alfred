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
public class CompareDocuments extends CompareCommand {

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        Comparator comparator = getComparator(corpus, cmd);

        int size = corpus.size();
        String[] ids = corpus.getIds();

        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
        for (int i = 0; i < ids.length; i++) {
            Report iReport = corpus.get(ids[i]);
            for (int j = 0; j < i; j++) {
                Report jReport = corpus.get(ids[j]);
                double similarity = comparator.compare(ids[i], ids[j]);
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
