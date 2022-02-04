package teammates.client.scripts;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import teammates.logic.api.TaskQueuer;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseStatisticsType;

public class FeedbackResponseStatisticsGenerateScript {

    private static void main(String args[]) {
        int YEAR_TO_START_CREATION = 2020;  // In production, this will be 2010.
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
    
        TaskQueuer taskQueuer = TaskQueuer.inst();
    
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
    }
}
