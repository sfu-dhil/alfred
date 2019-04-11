package ca.nines.alfred.comparator;

public interface Comparator {

    void add(String id, String content);

    void complete();

    double compare(String srcId, String dstId);

}
