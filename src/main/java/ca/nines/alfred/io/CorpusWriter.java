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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Write all the reports in a corpus to disk.
 */
public class CorpusWriter {

    protected static final Logger logger = LoggerFactory.getLogger(CorpusWriter.class);

    /**
     * Write the contents of the corpus to disk.
     *
     * @param corpus the set of reports to write out.
     * @throws IOException if the reports cannot be written.
     */
    public static void write(Corpus corpus) throws IOException {
        for(Report report : corpus) {
            if(report.getFile() == null) {
                logger.error("Cannot write report {} to disk: unknown file.", report.getId());
                continue;
            }
            FileUtils.writeStringToFile(report.getFile(), report.serialize(), "UTF-8");
        }
    }

}
