package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.util.Tokenizer;
import ca.nines.alfred.vsm.VectorSpaceModel;

import java.io.IOException;

public class VSMComparator extends Comparator {

    public static final double THRESHOLD = 0.9;

    private final VectorSpaceModel vsm;

    public VSMComparator(TextCollection collection, String stopWordsFile) throws IOException {
        super(collection, stopWordsFile);
        Tokenizer tokenizer = new Tokenizer(stopWordsFile);
        vsm = new VectorSpaceModel(tokenizer);
        for(String id : collection.keys()) {
            vsm.add(id, collection.get(id));
        }
        vsm.computeWeights();
    }

    public VSMComparator(VectorSpaceModel vsm) {
        super(null, null);
        this.vsm = vsm;
    }

    @Override
    public String getType() {
        return "vsm";
    }

    @Override
    public double compare(String aId, String bId) {
        double similarity = vsm.compare(aId, bId);
        if(similarity >= THRESHOLD) {
            return Math.min(1.0, similarity);
        } else {
            return 0.0;
        }
    }
}
