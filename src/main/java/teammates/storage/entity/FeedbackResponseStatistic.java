package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

@Entity
public class FeedbackResponseStatistic extends BaseEntity {
	@Id
    // Represents the middle of the minute
    private long time;

    @Index
    private int count;

    // Size of interval in seconds
    private final int interval;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    protected FeedbackResponseStatistic() {
        // required by Objectify
        
        // used to remove compilation error
        this.interval = 0;
    }

    public FeedbackResponseStatistic(long time, int count, int interval) {
        this.time = time;
        this.count = count;
        this.interval = interval;
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
    
    public int getInterval() {
        return interval;
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
