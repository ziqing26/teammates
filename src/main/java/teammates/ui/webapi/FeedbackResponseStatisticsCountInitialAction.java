package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/**
 * Task queue worker action: generates the feedback response statistics since the start of the course.
 */
public class FeedbackResponseStatisticsCountInitialAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();
    private static final int YEAR_TO_START_CREATION = 2010;

    @Override
    public JsonResult execute() {
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        LocalDateTime timeOfCreation = LocalDateTime.of(YEAR_TO_START_CREATION, 0, 0, 0, 0);

        Instant startOfCreation = timeOfCreation.toInstant(currentOffset);
        Instant endOfCreation = LocalDateTime.now().toInstant(currentOffset);

        // Check how many hours in between.
        Duration timeDifference = Duration.between(startOfCreation, endOfCreation);
        Long hoursDifference = Math.abs(timeDifference.toHours());
        Long minutesDifference = Math.abs(timeDifference.toMinutes());

        Instant startOfIntervalForHours = startOfCreation;
        Instant startOfIntervalForMinutes = startOfCreation;

        try {
            for (int i = 0; i < hoursDifference; i++) {
                taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForHours,
                        FeedbackResponseStatisticsType.HOUR);
                startOfIntervalForHours.plusSeconds(Const.HOUR_IN_SECONDS);
            }

            for (int i = 0; i < minutesDifference; i++) {
                taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForMinutes,
                        FeedbackResponseStatisticsType.MINUTE);
                startOfIntervalForMinutes.plusSeconds(Const.MINUTE_IN_SECONDS);
            }

        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }
}
