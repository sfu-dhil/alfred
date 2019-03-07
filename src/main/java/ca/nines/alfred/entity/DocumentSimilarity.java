package ca.nines.alfred.entity;

public class DocumentSimilarity {

    private String reportId;

    private double similarity;

    private String type;

    public DocumentSimilarity(String reportId, double similarity, String type) {
        this.reportId = reportId;
        this.similarity = similarity;
        this.type = type;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
