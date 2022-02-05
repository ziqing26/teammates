package teammates.client.scripts;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import teammates.client.connector.DatastoreClient;
import teammates.common.util.Const;
import teammates.logic.api.TaskQueuer;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/**
 * Generates hour and minute feedback response statistics object since a specific year.
 * Feedback response statistics after the year will also be recounted and written over.
 */
public class FeedbackResponseStatisticsGenerateScript extends DatastoreClient {

    private static final int YEAR_TO_START_CREATION = 2010;

    private FeedbackResponseStatisticsGenerateScript() {
    }

    @Override
    protected void doOperation() {
        generateResponses();
    }

    public static void main(String[] args) {
        new FeedbackResponseStatisticsGenerateScript().doOperationRemotely();
    }

    /**
     * Adds tasks for creation of feedback response statistics objects since YEAR_TO_START_CREATION.
     */
    public static void generateResponses() {
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        LocalDateTime timeOfCreation = LocalDateTime.of(YEAR_TO_START_CREATION, 1, 1, 0, 0);

        Instant startOfCreation = timeOfCreation.toInstant(currentOffset);
        Instant endOfCreation = LocalDateTime.now().toInstant(currentOffset);

        // Check how many hours in between.
        Duration timeDifference = Duration.between(startOfCreation, endOfCreation);
        Long hoursDifference = Math.abs(timeDifference.toHours());
        Long minutesDifference = Math.abs(timeDifference.toMinutes());

        Instant startOfIntervalForHours = startOfCreation;
        Instant startOfIntervalForMinutes = startOfCreation;

        TaskQueuer taskQueuer = TaskQueuer.inst();

        for (int i = 0; i < hoursDifference; i++) {
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForHours,
                    FeedbackResponseStatisticsType.HOUR);
            startOfIntervalForHours = startOfIntervalForHours.plusSeconds(Const.HOUR_IN_SECONDS);
        }

        for (int i = 0; i < minutesDifference; i++) {
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForMinutes,
                    FeedbackResponseStatisticsType.MINUTE);
            startOfIntervalForMinutes = startOfIntervalForMinutes.plusSeconds(Const.MINUTE_IN_SECONDS);
        }
    }
}
