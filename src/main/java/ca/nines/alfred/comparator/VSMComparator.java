package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.util.Tokenizer;
import ca.nines.alfred.util.TokenizerFactory;
import ca.nines.alfred.vsm.VectorSpaceModel;

public class VSMComparator extends Comparator {

    private final VectorSpaceModel vsm;

    public VSMComparator(Corpus corpus) {
        super(corpus);
        Tokenizer tokenizer = TokenizerFactory.getInstance();
        vsm = new VectorSpaceModel(tokenizer);
        for(Report report : corpus) {
            vsm.add(report.getId(), report.getComparableContent());
        }
    }

    @Override
    public String getType() {
        return "vsm";
    }

    @Override
    public double compare(Report a, Report b) {
        return 0;
    }
}
