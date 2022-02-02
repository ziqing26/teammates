package teammates.storage.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents the count of feedback responses in an hour.
 */
@Entity
@Index
public class FeedbackResponseStatisticHour extends FeedbackResponseStatistic {
}
