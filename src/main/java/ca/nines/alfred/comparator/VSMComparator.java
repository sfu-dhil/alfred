package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.util.Tokenizer;
import ca.nines.alfred.vsm.VectorSpaceModel;

import java.io.IOException;

public class VSMComparator extends Comparator {

    private final VectorSpaceModel vsm;

    public VSMComparator(Corpus corpus, String stopWordsFile) throws IOException {
        super(corpus, stopWordsFile);
        Tokenizer tokenizer = new Tokenizer(stopWordsFile);
        vsm = new VectorSpaceModel(tokenizer);
        for(Report report : corpus) {
            vsm.add(report.getId(), report.getComparableContent());
        }
        vsm.computeWeights();
    }

    @Override
    public String getType() {
        return "vsm";
    }

    @Override
    public double compare(Report a, Report b) {
        double similarity = vsm.compare(a.getId(), b.getId());
        if(similarity > 0.6) {
            return Math.min(1.0, similarity);
        } else {
            return 0.0;
        }
    }
}
