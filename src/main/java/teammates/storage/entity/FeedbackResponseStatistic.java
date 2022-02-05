package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents the count 
 */
@Entity
public class FeedbackResponseStatistic extends BaseEntity {
	@Id
    // Represents the start of the interval in epoch seconds
    private long time;

    @Index
    private int count;

    @Index
    // Represents the size of interval
    private FeedbackResponseStatisticsType intervalType;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    protected FeedbackResponseStatistic() {
        // required by Objectify
    }

    public FeedbackResponseStatistic(long time, int count, FeedbackResponseStatisticsType intervalType) {
        this.time = time;
        this.count = count;
        this.intervalType = intervalType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public FeedbackResponseStatisticsType getIntervalType() {
        return intervalType;
    }
	
	/**
     * Sets the createdAt timestamp.
     */
    public void setCreatedAt(Instant created) {
        this.createdAt = created;
        setLastUpdate(created);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

	public void setLastUpdate(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

	/**
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }

}
