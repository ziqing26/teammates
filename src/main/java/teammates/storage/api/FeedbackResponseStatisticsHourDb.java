package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponseStatisticHour;
import teammates.ui.webapi.FeedbackResponseStatisticsCountHourAction;

/**
 * Handles CRUD operations for FeedbackResponseStatisticsHour.
 *
 * @see FeedbackResponseStatisticHour
 * @see FeedbackResponseStatisticsCountHourAction
 */
public class FeedbackResponseStatisticsHourDb {
    private static final FeedbackResponseStatisticsHourDb instance = new FeedbackResponseStatisticsHourDb();

	public static FeedbackResponseStatisticsHourDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatisticHour.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
	}

	public LoadType<FeedbackResponseStatisticHour> load() {
		return ofy().load().type(FeedbackResponseStatisticHour.class); 
	}
	/**
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatisticHour statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatisticsInInterval(Instant startTime, Instant endTime) {
        // Query<FeedbackResponseStatisticHour> query = this.load().filterKey("time >= ", startTime)
        //         .filterKey("time <=", endTime);

        // List<FeedbackResponseStatisticAttributes> statistics = StreamSupport.stream(query.spliterator(), false)
		// 		.map(stats -> makeAttributes(stats))
		// 		.collect(Collectors.toList());
		Random rng = new Random();
		List<FeedbackResponseStatisticAttributes> statistics = new ArrayList<>();
		long intervals = Duration.between(startTime, endTime).toMinutes();
		for (long i = 0; i <= intervals; i++) {
			long time = startTime.plus(i, ChronoUnit.HOURS).getEpochSecond();
			int count = rng.nextInt(100);
			FeedbackResponseStatisticHour minuteEntity = new FeedbackResponseStatisticHour(time, count);
			statistics.add(makeAttributes(minuteEntity));
		}
				
		return statistics;
    }
}
