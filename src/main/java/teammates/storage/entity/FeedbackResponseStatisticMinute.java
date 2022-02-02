package teammates.storage.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class FeedbackResponseStatisticMinute extends FeedbackResponseStatistic {
	private static final int MINUTE_IN_SECONDS = 60;
	/**
     * Instantiates a new hourly response statistic.
     *
     * @param time
     *            the start of the minute, with ISO 8601 representation.
     * @param count
     *            the number of feedbacck responses in the time period.
     */
	public FeedbackResponseStatisticMinute(long time, Integer count) {
		super(MINUTE_IN_SECONDS);
        this.setTime(time);
        this.setCount(count);
    }
}
