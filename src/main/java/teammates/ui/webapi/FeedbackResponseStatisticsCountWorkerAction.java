package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.api.FeedbackResponseStatisticsDb;
import teammates.storage.entity.FeedbackResponseStatisticsType;

public class FeedbackResponseStatisticsCountWorkerAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_STATISTIC_STARTIME);
        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_STATISTIC_ENDTIME);
        String intervalTypeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_STATISTIC_TYPE);
        
        Instant intervalStartTime = Instant.ofEpochMilli(Long.parseLong(startTimeString));
        Instant intervalEndTime = Instant.ofEpochMilli(Long.parseLong(endTimeString));

        FeedbackResponseStatisticsType intervalType = Enum.valueOf(FeedbackResponseStatisticsType.class,
                intervalTypeString);
        System.out.println("Creating new counter! " + intervalStartTime + " " + intervalEndTime);
        try {
            FeedbackResponseStatisticsDb
                .inst()
                .countAndCreateStatisticsObject(intervalStartTime, intervalEndTime, intervalType);
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }
        return new JsonResult("Sucess!");
    }
}
