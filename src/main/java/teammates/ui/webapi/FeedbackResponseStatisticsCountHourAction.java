package teammates.ui.webapi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import teammates.common.util.Logger;
import teammates.storage.api.FeedbackResponseStatisticsDb;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/**
 *  Cron job: schedules feedback statistics count every hour.
 */
public class FeedbackResponseStatisticsCountHourAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        Instant intervalEndTime = LocalDateTime.now()
                                    .truncatedTo(ChronoUnit.SECONDS)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .toInstant(currentOffset);

        Instant intervalStartTime = intervalEndTime.minusSeconds(60 * 60);
        try {
            FeedbackResponseStatisticsDb
                .inst()
                    .countAndCreateStatisticsObject(intervalStartTime, intervalEndTime,
                            FeedbackResponseStatisticsType.HOUR);
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }
        return new JsonResult("Successful");
    }
}
