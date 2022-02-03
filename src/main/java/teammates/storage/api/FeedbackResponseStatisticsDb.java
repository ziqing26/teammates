package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.time.temporal.ChronoUnit;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseStatistic;

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
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatistic statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatisticsInInterval(Instant startTime, Instant endTime, int interval) {
		assert interval == Const.MINUTE_IN_SECONDS || interval == Const.HOUR_IN_SECONDS;
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
		if (interval == Const.MINUTE_IN_SECONDS) {
			intervals = timeDifference.toMinutes();
			timeUnit = ChronoUnit.MINUTES;
		} else {
			intervals = timeDifference.toHours();
			timeUnit = ChronoUnit.HOURS;
		}

		for (long i = 0; i <= intervals; i++) {
			long time = startTime.plus(i, timeUnit).getEpochSecond();
			int count = rng.nextInt(100);
			FeedbackResponseStatistic statisticEntity = new FeedbackResponseStatistic(time, count, interval);
			statistics.add(makeAttributes(statisticEntity));
		}
				
		return statistics;
    }
	
}
