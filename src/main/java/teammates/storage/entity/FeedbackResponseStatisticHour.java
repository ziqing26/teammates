package teammates.storage.entity;

import teammates.common.util.Const;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents the count of feedback responses in an hour.
 */
@Entity
@Index
public class FeedbackResponseStatisticHour extends FeedbackResponseStatistic {
	private static final int HOUR_IN_SECONDS = Const.HOUR_IN_SECONDS;
	/**
     * Instantiates a new hourly response statistic.
     *
     * @param time
     *            the middle of the minute, with ISO 8601 representation.
     * @param count
     *            the number of feedbacck responses in the time period.
     */
	public FeedbackResponseStatisticHour(long time, int count) {
		super(HOUR_IN_SECONDS);
        this.setTime(time);
        this.setCount(count);
    }
}
