package ca.nines.alfred.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public Iterator<Report> iterator() {
        return reports.values().iterator();
    }
}
