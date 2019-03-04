/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.cmd;

import ca.nines.alfred.annotator.PunctuationAnnotator;
import ca.nines.alfred.annotator.StemAnnotator;
import ca.nines.alfred.annotator.StopWordAnnotator;
import ca.nines.alfred.reader.HtmlReader;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Document;
import ca.nines.alfred.entity.Paragraph;
import ca.nines.alfred.entity.Sentence;
import ca.nines.alfred.nlp.NGramCounter;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael
 */
public class Html extends Command {

    public static final int NGRAM_SIZE = 4;

    public static final int LF_SIZE = 3;

    @Override
    public String getDescription() {
        return "Parse an HTML file and extract the text.";
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        List<String> argList = cmd.getArgList();
        if (argList.size() == 1) {
            System.err.println(getUsage());
            return;
        }
        Corpus corpus = Corpus.build(argList.get(1));
        NGramCounter counter = new NGramCounter();
        counter.add(corpus.ngrams(NGRAM_SIZE));
        for(Document document : corpus) {
            System.out.println("F: " + document.getFilename());
            for(Sentence sentence : document.getSentences()) {
                System.out.println("S: " + sentence.getText());
                for(String ngram : counter.leastFrequent(sentence.ngrams(NGRAM_SIZE), LF_SIZE)) {
                    System.out.println(ngram + " - " + counter.count(ngram));
                }
            }
        }
    }

    @Override
    public String getCommandName() {
        return "html";
    }

    @Override
    public String getUsage() {
        return "java -jar alfred parse FILE...";
    }

}
