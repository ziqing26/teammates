package teammates.client.scripts;

import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.FeedbackResponseStatisticsDb;
import teammates.storage.api.OfyHelper;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatistic;
import teammates.storage.entity.FeedbackResponseStatisticsType;

/*
*   Handles getting of the stats
*/
public class FergusTest extends DatastoreClient {

    // Runs the test
    private static final int HOUR = 60 * 60;
    private static final int WEEK = HOUR * 24 * 7;
    private static final int MONTH = WEEK * 4;
    private static final int YEAR = MONTH * 12;
    private static final int million = 1000000;
    private static final int tenmillion = 10000000;

    private static final String COURSE_ID = "TestData.500S30Q100T";

    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String GIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";

    private FergusTest() {
    }

    public static Integer getTotalStatisticsObjectCount() {
        int count = ObjectifyService.ofy().load().type(FeedbackResponseStatistic.class)
                .project("createdAt")
                .list()
                .size();
        System.out.println(count);
        return count;
    }
    
    public static void generateStatisticsObjects() {
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
            
        System.out.println("Scheduling...");
        for (int i = 0; i < hoursDifference; i++) {
            if (i % 100 == 0) {
                System.out.println("Hours " + i);
            }
            FeedbackResponseStatisticsDb
                .inst()
                    .countAndCreateStatisticsObject(startOfIntervalForHours,
                            startOfIntervalForHours.plusSeconds(Const.HOUR_IN_SECONDS),
                            FeedbackResponseStatisticsType.HOUR);

            startOfIntervalForHours = startOfIntervalForHours.plusSeconds(Const.HOUR_IN_SECONDS);
        }
        
        for (int i = 0; i < minutesDifference; i++) {
            if (i % 10000 == 0) {
                System.out.println("Minutes " + i);
            }
            FeedbackResponseStatisticsDb
                .inst()
                    .countAndCreateStatisticsObject(startOfIntervalForMinutes,
                            startOfIntervalForHours.plusSeconds(Const.MINUTE_IN_SECONDS),
                            FeedbackResponseStatisticsType.MINUTE);
            startOfIntervalForMinutes = startOfIntervalForMinutes.plusSeconds(Const.MINUTE_IN_SECONDS);
        }        
    }

    public static void getIntervalResponseCount() {
        long DEFAULT_INTERVAL = 50;
        Instant startTime = Instant.now().minusSeconds(YEAR);
        Instant endTime = Instant.now();
        long timeDifference = endTime.getEpochSecond() - startTime.getEpochSecond();
        long defaultIntervalSize = Math.floorDiv(timeDifference, DEFAULT_INTERVAL);
        long buffer = timeDifference - (defaultIntervalSize * DEFAULT_INTERVAL);

        Map<Instant, Integer> hashCount = new HashMap<>();
        Instant currentTime = startTime;
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class)
                .project("createdAt");
        int totalCount = 0;
        for (long i = 0; i < DEFAULT_INTERVAL; i++) {
            long secondsToNextInterval = buffer <= 0 ? defaultIntervalSize : defaultIntervalSize + 1;
            buffer -= 1;
            endTime = currentTime.plusSeconds(secondsToNextInterval);
            Integer count = intialQuery.filter("createdAt >", currentTime).filter("createdAt <", endTime).list().size();
            System.out.println(
                    "Doing " + i + "th with count " + count + " with interval " + currentTime + " to " + endTime);
            hashCount.put(currentTime, count);
            totalCount += count;
            currentTime = endTime;
        }
        System.out.println(hashCount);
        System.out.println(totalCount);
    }

    public static void getTotalResponseCount() {
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class);
        System.out.println("Total responses: " + intialQuery.count());
    }

    public static void deleteAllResponses() {
        Iterable<Key<FeedbackResponse>> allKeys = ObjectifyService.ofy().load().type(FeedbackResponse.class).keys();
        ObjectifyService.ofy().delete().keys(allKeys).now();
    }

    public static void generateResponses() {
        int STARTING_ID = 1;
        int NUMBER_OF_FEEDBACK_QUESTIONS = 1000;
        int CHUNKER = 10; // Prevent Java heap overflow
        for (int i = 0; i < CHUNKER; i++) {
            FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS / CHUNKER];
            for (int j = 0; j < NUMBER_OF_FEEDBACK_QUESTIONS / CHUNKER; j++) {
                int secondsOffset = (int) (Math.random() * YEAR);
                STARTING_ID++;
                FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                        generateId(Integer.toString(secondsOffset), Integer.toString(STARTING_ID)),
                        null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
                feedback.setCreatedAt(Instant.now().minusSeconds(secondsOffset));
                arr[j] = feedback;
            }
            System.out.println("Finished creating, now saving chunk " + i);
            ObjectifyService.ofy().save().entities(arr).now();
        }
        System.out.println("Finished generating!");
    }

    public static void generateResponsesNow() {
        int STARTING_ID = 1;
        int NUMBER_OF_FEEDBACK_QUESTIONS = 10;
        FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS];
        for (int i = 0; i < NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
            int randomNumber = (int) (Math.random() * YEAR);
            STARTING_ID++;
            FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                    generateId(Integer.toString(randomNumber), Integer.toString(STARTING_ID)),
                    null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
            feedback.setCreatedAt(Instant.now());
            arr[i] = feedback;
        }
        ObjectifyService.ofy().save().entities(arr).now();
    }

    public static String generateId(String feedbackQuestionId, String giver) {
        return feedbackQuestionId + '%' + giver;
    }

    public static void countAndCreateStatisticsObject(Instant intervalStartTime, Instant intervalEndTime,
    FeedbackResponseStatisticsType intervalType) {
        int count = ObjectifyService.ofy().load().type(FeedbackResponse.class)
            .project("createdAt")
            .filter("createdAt >", FeedbackResponseStatisticsDb.adjustIntervalStartTime(intervalStartTime))
            .filter("createdAt <", FeedbackResponseStatisticsDb.adjustIntervalEndTime(intervalEndTime))
            .list()
            .size();
        System.out.println(count);
        FeedbackResponseStatistic newEntry = new FeedbackResponseStatistic(
                intervalStartTime.getEpochSecond(), count, intervalType);
        ObjectifyService.ofy().save().entities(newEntry).now();
    }

    @Override
    protected void doOperation() {
        // generateResponses();
        // countAndCreateStatisticsObject();
        // getIntervalResponseCount(); //
        // getTotalResponseCount();
        // getTotalStatisticsObjectCount();

        // generateResponsesNow();
        // generateStatisticsObjects();
        // deleteAllResponses();
        // System.out.println(ZonedDateTime().now());
    }

    protected void doFergusOperationRemotely(Instant intervalStartTime, Instant intervalEndTime,
    FeedbackResponseStatisticsType intervalType) {

        String appUrl = ClientProperties.TARGET_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        if (ClientProperties.isTargetUrlDevServer()) {
            builder.setHost(ClientProperties.TARGET_URL);
        }
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();
        Closeable objectifySession = ObjectifyService.begin();
        LogicStarter.initializeDependencies();

        doFergusOperation(intervalStartTime, intervalEndTime,
                intervalType);
        try { 
            objectifySession.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("--- Remote operation completed ---");
    }

    protected void doFergusOperation(Instant intervalStartTime, Instant intervalEndTime,
            FeedbackResponseStatisticsType intervalType) {
        countAndCreateStatisticsObject(intervalStartTime, intervalEndTime, intervalType);
    }

    public static void fergusMain(Instant intervalStartTime, Instant intervalEndTime,
            FeedbackResponseStatisticsType intervalType) {
            new FergusTest().doFergusOperationRemotely(intervalStartTime, intervalEndTime, intervalType);
    }
    
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start timer at " + (startTime));
        new FergusTest().doOperationRemotely();
        /*         LocalDatastoreHelper localDatastoreHelper = LocalDatastoreHelper.newBuilder()
        .setConsistency(0.9) // default setting
        .setPort(Config.APP_LOCALDATASTORE_PORT)
        .setStoreOnDisk(true)
        .setDataDir(Paths.get("datastore-dev/datastore"))
        .build();
        try {
            localDatastoreHelper.start();
            DatastoreOptions options = localDatastoreHelper.getOptions();
            ObjectifyService.init(new ObjectifyFactory(
        options.getService()
            ));
            ObjectifyService.begin();
            OfyHelper.registerEntityClasses();
            LogicStarter.initializeDependencies();
            Closeable objectifySession = ObjectifyService.begin();
            System.out.println(localDatastoreHelper.getGcdPath());
            System.out.println(localDatastoreHelper.isStoreOnDisk());
            new FergusTest().doOperation();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            objectifySession.close();
        }
        }
         */
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds or " + ((endTime - startTime) / 1000)
                + " seconds or " + (((endTime - startTime) / 1000) / 60 + " minutes."));
    }

}
