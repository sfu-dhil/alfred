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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A corpus is a collection of reports.
 */
public class Corpus implements Iterable<Report> {

    Map<String, Report> reports;

    public Corpus() {
        reports = new HashMap<>();
    }

    public void add(Report report) {
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

    public TextCollection getCollection() {
        return getCollection(false);
    }

    public TextCollection getCollection(boolean paragraphs) {
        TextCollection collection = new TextCollection();
        for(Report report : reports.values()) {
            if(paragraphs) {
                for(String id : report.getParagraphIds()) {
                    collection.put(id, report.getParagraph(id));
                }
            } else {
                collection.put(report.getId(), report.getComparableContent());
            }
        }
        return collection;
    }

    @Override
    public Iterator<Report> iterator() {
        return reports.values().iterator();
    }
}
