package ca.nines.alfred.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class PipelineBuilder {

    public static StanfordCoreNLP build() {
        return build(null);
    }

    public static StanfordCoreNLP build(String stopwords) {
        Properties props = new Properties();
        String annotators = "tokenize";
        if(stopwords != null && !stopwords.isEmpty()) {
            annotators = "tokenize,ssplit,pos,lemma,stopword";
            props.put("stopword.wordlist", stopwords);
        }

        props.put("parse.keepPunct", true);
        props.put("annotators", annotators);
        props.put("coref.algorithm", "neural");
        props.put("ner.applyFineGrained", "0");
        props.put("customAnnotatorClass.stopword", StopWordAnnotator.class.getCanonicalName());
        props.put("customAnnotatorClass.stem", StemAnnotator.class.getCanonicalName());

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        return pipeline;
    }
}
