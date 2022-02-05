package teammates.ui.output;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;

/**
 * The API output format of {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponseStatisticData extends ApiOutput {

    private final long time;

    private final int count;

    public FeedbackResponseStatisticData(FeedbackResponseStatisticAttributes feedbackResponseStatisticAttributes) {
        this.time = feedbackResponseStatisticAttributes.getTime();
        this.count = feedbackResponseStatisticAttributes.getCount();
    }

    public long getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }
}
