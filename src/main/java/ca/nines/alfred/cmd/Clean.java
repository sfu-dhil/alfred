package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import ca.nines.alfred.io.CorpusWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

@CommandInfo(name="clean", description="Clean the files in a directory.")
public class Clean extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        opts.addOption("t", "translations", false, "Also remove translations");
        opts.addOption(null, "ids", false, "Also reset IDs on documents and paragraphs.");
        return opts;
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        int n = 0;
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for(Report report : corpus) {
            n++;
            String id = report.getFile().getName().replaceAll("[^A-Z]", "").toLowerCase() + "_" + n;

            if( ! report.hasId() || cmd.hasOption("ids")) {
                report.setId(id);
                report.setParagraphIds();
            }
            if(cmd.hasOption("translations")) {
                report.setTranslation(null);
            }
            report.removeDocumentSimilarities();
            report.removeParagraphSimilarities();
            report.setMetadata("wr.path", null);
            report.setMetadata("wr.word-count", null);
            report.setMetadata("wr.wordcount", null);
            report.setMetadata("index.paragraph", null);
            report.setMetadata("index.document", null);

            tick();
        }
        reset();
        CorpusWriter.write(corpus);
    }

}
