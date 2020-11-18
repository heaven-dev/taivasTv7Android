package fi.tv7.taivastv7.helpers;

public class GuideDate {
    private String date = null;
    private String label = null;

    public GuideDate(String date, String label) {
        this.date = date;
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public String getLabel() {
        return label;
    }
}
