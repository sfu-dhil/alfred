package ca.nines.alfred.cmd;

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.DocumentSimilarity;
import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;

@CommandInfo(name = "dc", description = "Document comparisons.")
public class CompareDocuments extends CompareCommand {

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        TextCollection collection = corpus.getCollection();
        Comparator comparator = getComparator(collection, cmd);

        long size = collection.size();
        String[] ids = collection.keys();

        out.println("Expect " + formatter.format(size * (size - 1) / 2) + " comparisons.");
        for (int i = 0; i < ids.length; i++) {
            for (int j = 0; j < i; j++) {
                double similarity = comparator.compare(ids[i], ids[j]);
                if(similarity > 0) {
                    corpus.get(ids[i]).addDocumentSimilarity(new DocumentSimilarity(ids[j], similarity, comparator.getType()));
                    corpus.get(ids[j]).addDocumentSimilarity(new DocumentSimilarity(ids[i], similarity, comparator.getType()));
                }
                tick();
            }
        }
        reset();
        CorpusWriter.write(corpus);
    }
}
