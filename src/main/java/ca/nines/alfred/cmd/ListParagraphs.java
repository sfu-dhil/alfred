package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

@ca.nines.alfred.cmd.CommandInfo(name = "lp", description = "List small paragraphs.")
public class ListParagraphs extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption(null, "size", true, "Show paragraphs with fewer letters.");
        return opts;
    }


    @Override
    public void execute(CommandLine cmd) throws Exception {
        int size = 64;
        if(cmd.hasOption("size")) {
            size = Integer.parseInt(cmd.getOptionValue("size"));
        }
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for(Report report : corpus) {
            for(String id : report.getParagraphIds()) {
                String content = report.getParagraph(id);
                if(content.length() <= size) {
                    out.println(content.length() + "\t" + report.getTitle() + "\t" + content);
                }
            }
        }
    }
}
