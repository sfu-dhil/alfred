package ca.nines.alfred.cmd;

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.comparator.CosineComparator;
import ca.nines.alfred.comparator.LevenshteinComparator;
import ca.nines.alfred.comparator.VSMComparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.ParagraphSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.List;

@CommandInfo(name = "pc", description = "Paragraph comparisons.")
public class CompareParagraphs extends CompareCommand {

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        Comparator comparator = getComparator(corpus, cmd);

        int size = corpus.size();
        String[] ids = corpus.getIds();

        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
        for (int i = 0; i < corpus.size(); i++) {
            Report iReport = corpus.get(ids[i]);
            String[] iIds = iReport.getParagraphIds();
            for (int j = 0; j < i; j++) {
                Report jReport = corpus.get(ids[j]);
                String[] jIds = jReport.getParagraphIds();

                for(String iId : iIds) {
                    for(String jId : jIds) {
                        double similarity = comparator.compare(iId, jId);
                        if(similarity > 0) {
                            iReport.addParagraphSimilarity(iId, new ParagraphSimilarity(jReport.getId(), jId, similarity, comparator.getType()));
                            jReport.addParagraphSimilarity(jId, new ParagraphSimilarity(iReport.getId(), iId, similarity, comparator.getType()));
                        }
                    }
                }

                tick();
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
