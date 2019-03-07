package ca.nines.alfred.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Corpus implements Iterable<Report> {

    List<Report> reports;

    public Corpus() {
        reports = new ArrayList<>();
    }

    public void add(Report report) {
        reports.add(report);
    }

    public void add(Corpus corpus) {
        this.reports.addAll(corpus.reports);
    }

    public Report get(int i) {
        return reports.get(i);
    }

    public int size() {
        return reports.size();
    }

    @Override
    public Iterator<Report> iterator() {
        return reports.iterator();
    }
}
