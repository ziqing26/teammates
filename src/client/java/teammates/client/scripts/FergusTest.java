package teammates.client.scripts;

import java.time.Instant;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatistic;

/**
 * 
 */
public class FergusTest extends DatastoreClient {

    // Runs the test
    private static final int HOUR = 60 * 60;
    private static final int WEEK = HOUR * 24 * 7;
    private static final int MONTH = WEEK * 4;
    private static final int YEAR = MONTH * 12;

    private static final String COURSE_ID = "TestData.500S30Q100T";

    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String GIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";

    private static final int YEAR_TO_START_CREATION = 2022;

    private static final int NUMBER_OF_FEEDBACK_QUESTIONS = 100000;

    private static final int TIME_TO_GENERATE = 2 * MONTH;

    private static final int STARTING_ID = 1;

    private FergusTest() {
    }

    /**
     * Prints the total count of all feedback responses in the database.
     */
    public static void getTotalResponseCount() {
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class);
        System.out.println("Total feedback responses: " + intialQuery.count());
    }

    /**
     * Prints the total count of all statistics object count in the database.
     */
    public static void getTotalStatisticsObjectCount() {
        int count = ObjectifyService.ofy().load().type(FeedbackResponseStatistic.class)
                .project("createdAt")
                .list()
                .size();
        System.out.println("Total feedback statistic objects: " + count);
    }

    /**
     * Delete all feedback responses in the database.
     */
    public static void deleteAllResponses() {
        Iterable<Key<FeedbackResponse>> allKeys = ObjectifyService.ofy().load().type(FeedbackResponse.class).keys();
        ObjectifyService.ofy().delete().keys(allKeys).now();
    }

    /**
     * Generates the NUMBER_OF_FEEDBACK_QUESTIONS over a period of TIME_TO_GENERATE.
     */
    public static void generateResponses() {
        int chunker = 10; // Prevent Java heap overflow
        int startingId = STARTING_ID;
        for (int i = 0; i < chunker; i++) {
            FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS / chunker];
            for (int j = 0; j < NUMBER_OF_FEEDBACK_QUESTIONS / chunker; j++) {
                int secondsOffset = (int) (Math.random() * TIME_TO_GENERATE);
                startingId++;
                FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                        generateId(Integer.toString(secondsOffset), Integer.toString(startingId)),
                        null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
                feedback.setCreatedAt(Instant.now().minusSeconds(secondsOffset));
                arr[j] = feedback;
            }
            System.out.println("Finished creating, now saving chunk " + i);
            ObjectifyService.ofy().save().entities(arr).now();
        }
        System.out.println("Finished generating!");
    }

    /**
     * Generates responses now.
     */
    public static void generateResponsesNow() {
        FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS];
        int startingId = STARTING_ID;
        for (int i = 0; i < NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
            int randomNumber = (int) (Math.random() * YEAR);
            startingId++;
            FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                    generateId(Integer.toString(randomNumber), Integer.toString(startingId)),
                    null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
            feedback.setCreatedAt(Instant.now());
            arr[i] = feedback;
        }
        ObjectifyService.ofy().save().entities(arr).now();
    }

    /**
     * Generates the id for the feedback response object.
     * Prevents hotspotting in the database.
     */
    private static String generateId(String feedbackQuestionId, String giver) {
        return feedbackQuestionId + '%' + giver;
    }

    @Override
    protected void doOperation() {
        generateResponses();
        // getIntervalResponseCount();
        getTotalResponseCount();
        // getTotalStatisticsObjectCount();

        // generateResponsesNow();
        // deleteAllResponses();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start timer at " + startTime);
        new FergusTest().doOperationRemotely();
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds or " + ((endTime - startTime) / 1000)
                + " seconds or " + (((endTime - startTime) / 1000) / 60 + " minutes."));
    }
}
