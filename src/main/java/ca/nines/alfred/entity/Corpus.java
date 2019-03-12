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
