package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatistic;

/**
 * Handles statistics at 1 minute intervals for FeedbackResponse.
 */
public class FeedbackResponseStatisticsMinuteDb extends EntitiesDb<FeedbackResponseStatistic, FeedbackResponseStatisticAttributes> {
	private static final FeedbackResponseStatisticsMinuteDb instance = new FeedbackResponseStatisticsMinuteDb();

	public static FeedbackResponseStatisticsMinuteDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	@Override
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatistic.class,
                        FeedbackResponse.generateId(entityToCreate.getFeedbackQuestionId(),
                                entityToCreate.getGiver(), entityToCreate.getRecipient())))
                .list()
                .isEmpty();
	}

	@Override
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
	
}
