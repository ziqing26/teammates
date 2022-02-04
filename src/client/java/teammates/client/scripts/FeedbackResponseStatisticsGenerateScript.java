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

public class FeedbackResponseStatisticsGenerateScript extends DatastoreClient {

    private FeedbackResponseStatisticsGenerateScript() {}

    @Override
    protected void doOperation() {
        generateResponses();
    }

    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start timer at " + (startTime));
        new FeedbackResponseStatisticsGenerateScript().doOperationRemotely();
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds or " +  ((endTime - startTime)/1000) + " seconds or " + (((endTime - startTime)/1000)/60 + " minutes.") );
    }

    public static void generateResponses() {
        int YEAR_TO_START_CREATION = 2022;  // In production, this will be 2010.
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
        
        System.out.println("Scheduling...");
        for (int i = 0; i < hoursDifference; i++) {
            if (i % 100 == 0) { System.out.println("Hours " + i);}
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForHours,
                FeedbackResponseStatisticsType.HOUR);
                startOfIntervalForHours.plusSeconds(Const.HOUR_IN_SECONDS);
        }
        
        for (int i = 0; i < minutesDifference; i++) {
            if (i % 10000 == 0) { System.out.println("Minutes " + i);}
            taskQueuer.scheduleFeedbackResponseStatisticsCreation(startOfIntervalForMinutes,
                FeedbackResponseStatisticsType.MINUTE);
                startOfIntervalForMinutes.plusSeconds(Const.MINUTE_IN_SECONDS);
        }        
    }
}
