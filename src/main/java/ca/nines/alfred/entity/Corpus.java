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

package ca.nines.alfred.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A corpus is a collection of reports.
 */
public class Corpus implements Iterable<Report> {

    Map<String, Report> reports;

    private int n = 0;

    private final Logger logger;

    public Corpus() {
        reports = new HashMap<>();
        logger = LoggerFactory.getLogger(getClass());
    }

    public void add(Report report) {
        if(reports.containsKey(report.getId())) {
            logger.info("Duplicate report key '" + report.getId() + "'");
            n++;
            report.setId("_" + n);
        }
        reports.put(report.getId(), report);
    }

    public void add(Corpus corpus) {
        for(Report report : corpus) {
            this.add(report);
        }
    }

    public String[] getIds() {
        Set<String> ids = reports.keySet();
        return ids.toArray(new String[ids.size()]);
    }

    public Report get(String id) {
        return reports.get(id);
    }

    public int size() {
        return reports.size();
    }

    @Override
    public Iterator<Report> iterator() {
        return reports.values().iterator();
    }
}
