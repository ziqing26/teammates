package teammates.storage.entity;

public enum FeedbackResponseStatisticsType {
    HOUR("HOUR"), MINUTE("MINUTE");

    private final String value;
    private FeedbackResponseStatisticsType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
