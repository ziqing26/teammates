package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponseStatisticMinute;

/**
 * Handles statistics at 1 minute intervals for FeedbackResponse.
 */
public class FeedbackResponseStatisticsMinuteDb extends EntitiesDb<FeedbackResponseStatisticMinute, FeedbackResponseStatisticAttributes<FeedbackResponseStatisticMinute>> {
	private static final FeedbackResponseStatisticsMinuteDb instance = new FeedbackResponseStatisticsMinuteDb();

	public static FeedbackResponseStatisticsMinuteDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	@Override
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes<FeedbackResponseStatisticMinute> feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatisticMinute.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
	}

	@Override
	public LoadType<FeedbackResponseStatisticMinute> load() {
		return ofy().load().type(FeedbackResponseStatisticMinute.class); 
	}
	/**
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes<FeedbackResponseStatisticMinute> makeAttributes(FeedbackResponseStatisticMinute statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}

    /**
     * Gets the feedback statistics .
     */
    public List<FeedbackResponseStatisticMinute> getFeedbackResponseStatisticsInInterval(Instant startTime, Instant endTime) {
        Query<FeedbackResponseStatisticMinute> query = this.load().filterKey("time >= ", startTime)
                .filterKey("time <=", endTime);

        List<FeedbackResponseStatisticMinute> statistics = StreamSupport.stream(query.spliterator(), false)
				.collect(Collectors.toList());
				
		return statistics;
    }
	
}
