package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponseStatistic;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/**
 * Handles statistics at 1 minute intervals for FeedbackResponse.
 */
public class FeedbackResponseStatisticsDb extends EntitiesDb<FeedbackResponseStatistic, FeedbackResponseStatisticAttributes> {
	private static final FeedbackResponseStatisticsDb instance = new FeedbackResponseStatisticsDb();

	public static FeedbackResponseStatisticsDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatistic.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
	}

	public LoadType<FeedbackResponseStatistic> load() {
		return ofy().load().type(FeedbackResponseStatistic.class);
	}
	
	/**
	 * Adjusts support > filter operation for projection queries
	 */
	public Instant adjustIntervalStartTime(Instant startOfInterval) {
		return startOfInterval.minusMillis(1);
	}

	/**
	 * Adjusts to support < filter operation for projection queries
	 */
	public Instant adjustIntervalEndTime(Instant endOfInterval) {
		return endOfInterval.plusMillis(1);
	}
	
	public void countAndCreateStatisticsObject(Instant intervalStartTime, Instant intervalEndTime,
			FeedbackResponseStatisticsType intervalType) {
		int count = load()
			.project("createdAt")
			.filter("createdAt >", adjustIntervalStartTime(intervalStartTime))
			.filter("createdAt <", adjustIntervalEndTime(intervalEndTime))
			.list()
			.size();
	
		FeedbackResponseStatistic newEntry = new FeedbackResponseStatistic(
			intervalStartTime.getEpochSecond(), count, intervalType);
		ObjectifyService.ofy().save().entities(newEntry).now();
	}
	
	/**
	 * Converts from entity to attributes.
	 */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatistic statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics.
     */
	public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatisticsInInterval(Instant startTime,
			Instant endTime, FeedbackResponseStatisticsType intervalType) {
		assert intervalType == FeedbackResponseStatisticsType.MINUTE || intervalType == FeedbackResponseStatisticsType.HOUR;
        // Query<FeedbackResponseStatisticMinute> query = this.load().filterKey("time >= ", startTime)
        //         .filterKey("time <=", endTime);

        // List<FeedbackResponseStatisticAttributes> statistics = StreamSupport.stream(query.spliterator(), false)
		// 		.map(stats -> makeAttributes(stats))
		// 		.collect(Collectors.toList());

		Random rng = new Random();
		List<FeedbackResponseStatisticAttributes> statistics = new ArrayList<>();
		Duration timeDifference = Duration.between(startTime, endTime);

		long intervals;
		ChronoUnit timeUnit;
		if (intervalType == FeedbackResponseStatisticsType.MINUTE) {
			intervals = timeDifference.toMinutes();
			timeUnit = ChronoUnit.MINUTES;
		} else {
			intervals = timeDifference.toHours();
			timeUnit = ChronoUnit.HOURS;
		}

		for (long i = 0; i <= intervals; i++) {
			long time = startTime.plus(i, timeUnit).getEpochSecond();
			int count = rng.nextInt(100);
			FeedbackResponseStatistic statisticEntity = new FeedbackResponseStatistic(time, count, intervalType);
			statistics.add(makeAttributes(statisticEntity));
		}
				
		return statistics;
    }
	
}
