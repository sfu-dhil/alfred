package ca.nines.alfred.wc;

import ca.nines.alfred.nlp.PipelineBuilder;
import ca.nines.alfred.nlp.StopWordAnnotator;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class NlpWordCounter extends WordCounter {

    private StanfordCoreNLP pipeline;

    public NlpWordCounter() {
        super();
        pipeline = PipelineBuilder.build();
    }

    public NlpWordCounter(String stopWordFile) {
        super(stopWordFile);
        pipeline = PipelineBuilder.build(stopWordFile);
    }

    public long count(String text) {
        CoreDocument coreDoc = new CoreDocument(text);
        pipeline.annotate(coreDoc);
        long wordCount = 0;
        for(CoreLabel token : coreDoc.tokens()) {
            if(hasStopWords && token.get(StopWordAnnotator.class)) {
                continue;
            }
            wordCount++;
        }
        return wordCount;
    }

}
