package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class FeedbackResponseStatisticMinute extends FeedbackResponseStatistic {
}
