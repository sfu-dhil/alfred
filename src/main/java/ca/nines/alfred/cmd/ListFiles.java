package ca.nines.alfred.cmd;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import ca.nines.alfred.io.CorpusReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import java.io.File;

import static org.apache.commons.io.FileUtils.*;

@CommandInfo(name="list", description="List the reports in a directory.")
public class ListFiles extends Command {

    @Override
    public Options getOptions() {
        Options opts = super.getOptions();
        Option type = new Option("t", "type", true,"File types to list.");
        type.setArgs(Option.UNLIMITED_VALUES);
        opts.addOption(type);
        return opts;
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        Corpus corpus = CorpusReader.read(getArgList(cmd));
        for(Report report : corpus) {
            System.out.println(report.getFile().getPath());
        }
    }

}
