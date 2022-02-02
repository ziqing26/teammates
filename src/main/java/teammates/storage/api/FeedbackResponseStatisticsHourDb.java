package teammates.storage.api;

import java.util.List;

import com.googlecode.objectify.Key;

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
	@Override
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatisticsHour.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
	}

	@Override
	public LoadType<FeedbackResponseStatisticsHour> load() {
		return ofy().load().type(FeedbackResponseStatisticsHour.class); 
	}
	/**
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatisticsHour statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics .
     */
    public List<FeedbackResponseStatisticsHour> getFeedbackResponseStatisticsInInterval(Instant startTime, Instant endTime) {
        Query<FeedbackResponseStatisticsHour> query = this.load().filterKey("time >= ", startTime)
                .filterKey("time <=", endTime);

        List<FeedbackResponseStatisticsHour> statistics = StreamSupport.stream(query.spliterator(), false)
                .collect(Collectors.toList());
    }
}
