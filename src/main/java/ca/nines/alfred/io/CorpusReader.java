/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package ca.nines.alfred.io;

import ca.nines.alfred.entity.Corpus;
import ca.nines.alfred.entity.Report;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Read reports from disk and return them.
 */
public class CorpusReader {

    /**
     * Read the XML files in one or more directories and return the reports they contain.
     *
     * @param roots paths to the report directories
     * @return the parsed reports
     * @throws IOException if the reports cannot be read
     */
    public static Corpus read(String roots[]) throws IOException {
        Corpus corpus = new Corpus();
        for(String root : roots) {
            corpus.add(read(root));
        }
        return corpus;
    }

    /**
     * Read the files in one or more directories and return the reports they contain.
     *
     * @param roots paths to the report directories
     * @param extensions file extensions the reports may have
     * @return the parsed reports
     * @throws IOException if the reports cannot be read
     */
    public static Corpus read(String roots[], String[] extensions) throws IOException {
        Corpus corpus = new Corpus();
        for(String root : roots) {
            corpus.add(read(root, extensions));
        }
        return corpus;
    }

    /**
     * Read the XML reports in a directory and return them
     *
     * @param root path to the root directory containing the reports
     * @return the corpus containing the reports from the root directory
     * @throws IOException if the reports cannot be read
     */
    public static Corpus read(String root) throws IOException {
        return CorpusReader.read(root, new String[]{"xml"});
    }

    /**
     * Read the reports in a directory and return them.
     *
     * @param root path to the root directory containing the reports
     * @param extensions file extensions the reports may have
     * @return the corpus containing the reports from the root directory
     * @throws IOException if the reports cannot be read
     */
    public static Corpus read(String root, String[] extensions) throws IOException {
        Corpus corpus = new Corpus();
        for(File file : FileUtils.listFiles(new File(root), extensions, true)) {
            corpus.add(Report.read(file));
        }
        return corpus;
    }

}
