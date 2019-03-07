package ca.nines.alfred.io;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class CorpusWriter {

    public static void write(Corpus corpus) throws IOException {
        for(Report report : corpus) {
            FileUtils.writeStringToFile(report.getFile(), report.serialize(), "UTF-8");
        }
    }

}
