package teammates.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackResponseStatisticsMinuteDb;

/**
 * Handles operations related to feedback response statistics.
 */
public class FeedbackResponseStatisticLogic {

	private static final FeedbackResponseStatisticLogic instance = new FeedbackResponseStatisticLogic();

	private final FeedbackResponseStatisticsMinuteDb feedbackResponseStatisticDb = FeedbackResponseStatisticsMinuteDb.inst();
	private final FeedbackResponseStatisticsHourDb feedbackResponseStatisticHourDb = FeedbackResponseStatisticsHourDb.inst();
	

	private FeedbackResponseStatisticLogic() {
		// prevent initialization
	}
	
	public static FeedbackResponseStatisticLogic inst() {
		return instance;
	}

	/**
     * Gets all feedback response statistics in time period.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatistics(Instant startTime, Instant endTime) {

		if (Duration.between(startTime, endTime).compareTo(Const.FEEDBACK_STATISTIC_MINUTE_THRESHHOLD) <= 0) {
			return FeedbackResponseStatisticsMinuteDb.getFeedbackResponseStatisticsInInterval(startTime, endTime);
		}

		return FeedbackResponseStatisticsMinuteDb.getFeedbackResponseStatisticsInInterval(startTime, endTime);
    }
}
