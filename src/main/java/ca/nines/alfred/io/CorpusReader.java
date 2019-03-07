package ca.nines.alfred.io;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CorpusReader {

    public static Corpus read(String roots[]) throws IOException {
        Corpus corpus = new Corpus();
        for(String root : roots) {
            corpus.add(read(root));
        }
        return corpus;
    }

    public static Corpus read(String roots[], String[] extensions) throws IOException {
        Corpus corpus = new Corpus();
        for(String root : roots) {
            corpus.add(read(root, extensions));
        }
        return corpus;
    }

    public static Corpus read(String root) throws IOException {
        return CorpusReader.read(root, new String[]{"xml"});
    }

    public static Corpus read(String root, String[] extensions) throws IOException {
        Corpus corpus = new Corpus();
        for(File file : FileUtils.listFiles(new File(root), extensions, true)) {
            corpus.add(Report.read(file));
        }
        return corpus;
    }

}
