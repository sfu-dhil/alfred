package ca.nines.alfred.cmd;

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.ParagraphSimilarity;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;

@CommandInfo(name = "pc", description = "Paragraph comparisons.")
public class CompareParagraphs extends CompareCommand {

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        TextCollection collection = corpus.getCollection(true);
        Comparator comparator = getComparator(collection, cmd);

        long size = collection.size();
        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");

        String[] reportIds = corpus.getIds();
        for (int i = 0; i < reportIds.length; i++) {
            Report src = corpus.get(reportIds[i]);
            String[] srcIds = src.getParagraphIds();

            for (int j = 0; j < i; j++) {
                Report dst = corpus.get(reportIds[j]);
                String[] dstIds = dst.getParagraphIds();

                for (String iId : srcIds) {
                    for (String jId : dstIds) {
                        double similarity = 0;
                        try {
                            similarity = comparator.compare(iId, jId);
                        } catch (IllegalArgumentException e) {
                            logger.error("Cannot compare {} to {}: {}", iId, jId, e.getMessage());
                        }
                        tick();
                        if (similarity <= 0) {
                            continue;
                        }
                        src.addParagraphSimilarity(iId, new ParagraphSimilarity(
                                reportIds[j], jId, similarity, comparator.getType()
                        ));
                        dst.addParagraphSimilarity(jId, new ParagraphSimilarity(
                                reportIds[i], iId, similarity, comparator.getType()
                        ));
                    }
                }
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
