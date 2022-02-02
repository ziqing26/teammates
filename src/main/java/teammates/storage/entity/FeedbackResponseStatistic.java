package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

abstract class FeedbackResponseStatistic extends BaseEntity {
	@Id
    // Represents the middle of the minute
    private String time;

    private Integer count;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    protected FeedbackResponseStatistic() {
        // required by Objectify
    }

    /**
     * Instantiates a new account.
     *
     * @param time
     *            the middle of the minute, with ISO 8601 representation.
     * @param count
     *            the number of feedbacck responses in the minute.
     */
    public FeedbackResponseStatistic(String time, Integer count) {
        this.setTime(time);
        this.setCount(count);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Instant getCreatedAt() {
		return createdAt;
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
