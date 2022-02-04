package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import teammates.common.util.Logger;

/**
 * Task queue worker action: prepares session unpublished reminder for a particular session to be sent.
 */
public class FeedbackResponseStatisticsCountInitialAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        int YEAR_TO_START_CREATION = 2010;
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        LocalDateTime timeOfCreation = LocalDateTime.of(YEAR_TO_START_CREATION, 0, 0, 0, 0)
        Instant startOfCreation = timeOfCreation.toInstant(currentOffset);
        // Create hour and minute
        Instant endOfCreation = LocalDateTime.now().toInstant(currentOffset);

        // Check how many hours in between.
        Duration timeDifference = Duration.between(startOfCreation, endOfCreation);
        Long hoursDifference = Math.abs(timeDifference.toHours());
        Long minutesDifference = Math.abs(timeDifference.toMinutes());
        
        Instant startOfTimeForHours = startOfCreation;
        for (int i = 0; i < hoursDifference; i++) {
            
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfCreation);
        }

        Instant startOfTimeForMinutes = startOfCreation;
        for (int i = 0; i < minutesDifference; i++) {
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfCreation);
        }

      return new JsonResult("Successful");
    }
}
