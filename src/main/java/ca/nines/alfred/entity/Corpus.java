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

import javax.annotation.Nonnull;
import java.util.*;

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
        n++;
        if(report.getId().isEmpty()) {
            report.setId("_" + n);
        }
        if(reports.containsKey(report.getId())) {
            logger.info("Duplicate report key '" + report.getId() + "'");
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
    @Nonnull
    public Iterator<Report> iterator() {
        List<Report> list = new ArrayList<>(reports.values());
        list.sort(new Comparator<>() {
            @Override
            public int compare(Report o1, Report o2) {
                return o1.getFile().getPath().compareTo(o2.getFile().getPath());
            }
        });
        return list.iterator();
    }
}
