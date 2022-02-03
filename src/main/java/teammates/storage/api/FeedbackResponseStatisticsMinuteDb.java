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
import teammates.storage.entity.FeedbackResponseStatisticMinute;

/**
 * Handles statistics at 1 minute intervals for FeedbackResponse.
 */
public class FeedbackResponseStatisticsMinuteDb {
	private static final FeedbackResponseStatisticsMinuteDb instance = new FeedbackResponseStatisticsMinuteDb();

	public static FeedbackResponseStatisticsMinuteDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatisticMinute.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
	}

	public LoadType<FeedbackResponseStatisticMinute> load() {
		return ofy().load().type(FeedbackResponseStatisticMinute.class); 
	}
	/**
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatisticMinute statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatisticsInInterval(Instant startTime, Instant endTime) {
        // Query<FeedbackResponseStatisticMinute> query = this.load().filterKey("time >= ", startTime)
        //         .filterKey("time <=", endTime);

        // List<FeedbackResponseStatisticAttributes> statistics = StreamSupport.stream(query.spliterator(), false)
		// 		.map(stats -> makeAttributes(stats))
		// 		.collect(Collectors.toList());
		Random rng = new Random();
		List<FeedbackResponseStatisticAttributes> statistics = new ArrayList<>();
		long intervals = Duration.between(startTime, endTime).toMinutes();
		for (long i = 0; i <= intervals; i++) {
			long time = startTime.plus(i, ChronoUnit.MINUTES).getEpochSecond();
			int count = rng.nextInt(100);
			FeedbackResponseStatisticMinute minuteEntity = new FeedbackResponseStatisticMinute(time, count);
			statistics.add(makeAttributes(minuteEntity));
		}
				
		return statistics;
    }
	
}
