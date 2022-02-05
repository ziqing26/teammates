package teammates.storage.entity;

/**
 * The time-interval of the a feedback session.
 */
public enum FeedbackResponseStatisticsType {
    // CHECKSTYLE.OFF:JavadocVariable
    HOUR("HOUR"), MINUTE("MINUTE");
    // CHECKSTYLE.ON:JavadocVariable

    private final String value;

    FeedbackResponseStatisticsType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
