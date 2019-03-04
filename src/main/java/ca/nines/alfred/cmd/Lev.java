/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.cmd;

import ca.nines.alfred.compare.Levenshteiner;
import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Document;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 *
 * @author mjoyce
 */
public class Lev extends Command {

    @Override
    public String getDescription() {
        return "Run a levenshtein compare on the corpus.";
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        List<String> argList = cmd.getArgList();
        if (argList.size() == 1) {
            System.err.println(getUsage());
            return;
        }

        Corpus corpus = Corpus.build(argList.get(1));
        for(int i = 0; i < corpus.size(); i++) {
            Document di = corpus.get(i);
            for(int j = 0; j < i; j++) {
                Document dj = corpus.get(j);
                double similarity = Levenshteiner.compare(di.getNormalizedText(), dj.getNormalizedText());
                if(similarity > Levenshteiner.THRESHOLD) {
                    // System.out.println(di.getFilename() + ":" + dj.getFilename() + ":" + similarity);
                }
                tick();
            }
        }
    }

    @Override
    public String getCommandName() {
        return "lev";
    }

    @Override
    public String getUsage() {
        return "java -jar alfred lev --doc|--par dir";
    }

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(new Option(null, "par", false, "Run the comparison at the paragraph level."));
        return opts;
    }

}
