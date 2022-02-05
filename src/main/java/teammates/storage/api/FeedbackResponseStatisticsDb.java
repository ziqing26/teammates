package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatistic;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/**
 * Handles statistics at intervals for FeedbackResponse.
 */
public class FeedbackResponseStatisticsDb
        extends EntitiesDb<FeedbackResponseStatistic,
        FeedbackResponseStatisticAttributes> {

    private static final FeedbackResponseStatisticsDb instance = new FeedbackResponseStatisticsDb();

    public static FeedbackResponseStatisticsDb inst() {
        return instance;
    }

    /**
     * * Checks whether there are existing entities in the database.
     */
    @Override
    public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
        return !load()
                .filterKey(Key.create(FeedbackResponseStatistic.class, feedbackResponseStatistic.getTime()))
                .list()
                .isEmpty();
    }

    @Override
    public LoadType<FeedbackResponseStatistic> load() {
        return ofy().load().type(FeedbackResponseStatistic.class);
    }

    /**
     * Adjusts support > filter operation for projection queries.
     */
    public static Instant adjustIntervalStartTime(Instant startOfInterval) {
        return startOfInterval.minusMillis(1);
    }

    /**
     * Adjusts to support < filter operation for projection queries.
     */
    public static Instant adjustIntervalEndTime(Instant endOfInterval) {
        return endOfInterval.plusMillis(1);
    }

    /**
     * Creates a feedback response statistic object by quering the feedback response in the interval.
     */
    public void countAndCreateStatisticsObject(Instant intervalStartTime, Instant intervalEndTime,
            FeedbackResponseStatisticsType intervalType) {
        int count = ofy().load().type(FeedbackResponse.class)
                .project("createdAt")
                .filter("createdAt >", adjustIntervalStartTime(intervalStartTime))
                .filter("createdAt <", adjustIntervalEndTime(intervalEndTime))
                .list()
                .size();
        FeedbackResponseStatistic newEntry = new FeedbackResponseStatistic(
                intervalStartTime.getEpochSecond(), count, intervalType);
        ofy().save().entities(newEntry).now();
    }

    /**
     * Converts from entity to attributes.
     */
    @Override
    public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatistic statistic) {
        assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
    }

    /**
     * Gets the feedback statistics.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatisticsInInterval(Instant startTime,
            Instant endTime, FeedbackResponseStatisticsType intervalType) {
        assert intervalType == FeedbackResponseStatisticsType.MINUTE
                || intervalType == FeedbackResponseStatisticsType.HOUR;

        Query<FeedbackResponseStatistic> query = load().filter("intervalType =", intervalType.getValue())
                .filterKey(">= ", Key.create(FeedbackResponseStatistic.class, startTime.getEpochSecond()))
                .filterKey("<=", Key.create(FeedbackResponseStatistic.class, endTime.getEpochSecond()));

        List<FeedbackResponseStatistic> stats = query.list();
        return makeAttributes(stats);
    }
}
