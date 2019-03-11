package ca.nines.alfred.entity;

public class ParagraphSimilarity {

    private String reportId;

    private String paragraphId;

    private double similarity;

    private String type;

    public ParagraphSimilarity(String reportId, String paragraphId, double similarity, String type) {
        this.reportId = reportId;
        this.paragraphId = paragraphId;
        this.similarity = similarity;
        this.type = type;
    }

    public String getReportId() {
        return reportId;
    }

    public String getParagraphId() {
        return paragraphId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public String getType() {
        return type;
    }
}
