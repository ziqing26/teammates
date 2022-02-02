package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.FeedbackResponseStatistic;
import teammates.storage.entity.FeedbackResponseStatisticHour;

public class FeedbackResponseStatisticAttributes<T extends FeedbackResponseStatistic> extends EntityAttributes<T> {
    // Interval of statistic measured in seconds 
    private final int interval;
	private long time;
    private int count;
    private transient Instant createdAt;
    private transient Instant updatedAt;

	private FeedbackResponseStatisticAttributes(long time, int count, int interval) {
		this.time = time;
        this.count = count;
        this.interval = interval;

        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }


	@Override
    public T toEntity() {
        if (interval == Const.HOUR_IN_SECONDS) {
            return new FeedbackResponseStatisticHour(time, count);
        } else if (interval == Const.MINUTE_IN_SECONDS) {
            return new FeedbackResponseStatisticMinutes(time, count); 
        }
	}
	
    /**
     * Gets the {@link FeedbackResponseStatisticAttributes} instance of the given {@link FeedbackResponseStatistic}.
     */
    public static FeedbackResponseStatisticAttributes valueOf(FeedbackResponseStatistic statistic) {
        FeedbackResponseStatisticAttributes statisticAttributes =
                new FeedbackResponseStatisticAttributes(statistic.getTime(), statistic.getCount());

        if (statistic.getCreatedAt() != null) {
            statisticAttributes.createdAt = statistic.getCreatedAt();
        }
        if (statistic.getUpdatedAt() != null) {
            statisticAttributes.updatedAt = statistic.getUpdatedAt();
        }

        return statisticAttributes;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("feedback response statistic time", time), errors);

        return errors;
    }

    // TODO How to sort by time
    /**
     * Sorts the instructors list alphabetically by name.
     */
	public static void sortByTime(List<FeedbackResponseStatisticAttributes<T>> statistics) {
		statistics.sort(Comparator.comparing(statistic -> statistic.getTime()));
	}

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.time).append(this.count);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackResponseStatisticAttributes otherStatistic = (FeedbackResponseStatisticAttributes) other;
            return Objects.equals(this.time, otherStatistic.time)
                    && Objects.equals(this.count, otherStatistic.count)
                    && Objects.equals(this.interval, otherStatistic.interval);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
		this.time = time;
    }

    public int getCount() {
        return count;
    }

	public void setCount(int count) {
		this.count = count;
	}
}
