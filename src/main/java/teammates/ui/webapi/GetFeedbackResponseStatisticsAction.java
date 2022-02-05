package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackResponseStatisticsData;

/**
 * Searches for instructors.
 */
class GetFeedbackResponseStatisticsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_STATISTIC_STARTIME);
        long startTime;
        try {
            startTime = Long.parseLong(startTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid startTime parameter", e);
        }
        try {
            Instant.ofEpochMilli(startTime).minus(Const.FEEDBACK_SESSIONS_STATISTICS_SEARCH_WINDOW).toEpochMilli();
        } catch (ArithmeticException e) {
            throw new InvalidHttpParameterException("Invalid startTime parameter", e);
        }

        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_STATISTIC_ENDTIME);
        long endTime;
        try {
            endTime = Long.parseLong(endTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid endTime parameter", e);
        }
        try {
            Instant.ofEpochMilli(endTime).plus(Const.FEEDBACK_SESSIONS_STATISTICS_SEARCH_WINDOW).toEpochMilli();
        } catch (ArithmeticException e) {
            throw new InvalidHttpParameterException("Invalid endTime parameter", e);
        }

        if (startTime > endTime) {
            throw new InvalidHttpParameterException(
                    "The filter range is not valid. End time should be after start time.");
        }

        List<FeedbackResponseStatisticAttributes> feedbackResponseStatistics =
                logic.getFeedbackResponseStatistics(Instant.ofEpochSecond(startTime), Instant.ofEpochSecond(endTime));

        FeedbackResponseStatisticAttributes.sortByTime(feedbackResponseStatistics);

        FeedbackResponseStatisticsData feedbackResponseStatisticsData = new
                FeedbackResponseStatisticsData(feedbackResponseStatistics);

        return new JsonResult(feedbackResponseStatisticsData);
    }
}
