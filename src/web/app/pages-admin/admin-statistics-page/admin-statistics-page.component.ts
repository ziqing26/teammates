import { Component, OnInit } from '@angular/core';
import { expand, finalize, reduce } from 'rxjs/operators';
import { ResponseStatisticsService } from 'src/web/services/response-statistics.service';
import { StatusMessageService } from 'src/web/services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  QueryLogsParams,
  FeedbackResponseStatistics,
  FeedbackResponseStatistic,
} from '../../../types/api-output';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { StatisticsChartDataModel } from '../../components/statistics-chart/ststistics-chart-model';
import { TimeFormat } from '../../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../../error-message-output';

interface SearchStatisticsFormModel {
  statisticsDateFrom: DateFormat;
  statisticsDateTo: DateFormat;
  statisticsTimeFrom: TimeFormat;
  statisticsTimeTo: TimeFormat;
}
@Component({
  selector: 'tm-admin-statistics-page',
  templateUrl: './admin-statistics-page.component.html',
  styleUrls: ['./admin-statistics-page.component.scss']
})

export class AdminStatisticsPageComponent implements OnInit {
  // data = {
  //   const text = await FileAttachment("temperature.csv").text();
  //   const parseDate = d3.utcParse("%Y-%m-%d");
  //   return d3.csvParse(text, ({date, submissions}) => ({
  //     date: parseDate(date),
  //     submissions: submissions
  //   }));
  // }
  // isAdmin: boolean = false;

  formModel: SearchStatisticsFormModel = {
    statisticsDateFrom: { year: 0, month: 0, day: 0 },
    statisticsTimeFrom: { hour: 0, minute: 0 },
    statisticsDateTo: { year: 0, month: 0, day: 0 },
    statisticsTimeTo: { hour: 0, minute: 0 },
  };
  queryParams: Partial<QueryLogsParams> = { startTime: 0, endTime: 0 };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  chartResult: StatisticsChartDataModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;
  // isFiltersExpanded: boolean = false;
  // searchStartTime: number = 0;
  // searchEndTime: number = 0;
  // earliestStatisticsTimestampRetrieved: number = Number.MAX_SAFE_INTEGER;
  // latestStatisticsTimestampRetrieved: number = 0;
  statisticsMap: Map<string, number> = new Map<string, number>();

  constructor(
    private timezoneService: TimezoneService,
    private responseStatisticsService: ResponseStatisticsService,
    private statusMessageService: StatusMessageService,
    ) { }

  ngOnInit(): void {
    this.isLoading = true;
    const now: Date = new Date();
    this.dateToday.year = now.getFullYear();
    this.dateToday.month = now.getMonth() + 1;
    this.dateToday.day = now.getDate();

    // Start with response statistics from yesterday
    const fromDate: Date = new Date(now.getTime() - 24 * 60 * 60 * 1000);

    this.formModel.statisticsDateFrom = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };

    this.formModel.statisticsDateTo = { ...this.dateToday };
    this.formModel.statisticsTimeFrom = { hour: fromDate.getHours(), minute: fromDate.getMinutes() };
    this.formModel.statisticsTimeTo = { hour: now.getHours(), minute: now.getMinutes() };
    
  }

  generateGraph(){
    const timestampFrom: number = this.timezoneService.resolveLocalDateTime(
      this.formModel.statisticsDateFrom, this.formModel.statisticsTimeFrom);
    const timestampUntil: number = this.timezoneService.resolveLocalDateTime(
      this.formModel.statisticsDateTo, this.formModel.statisticsTimeFrom);
    
    this.searchForStatistics(timestampFrom, timestampUntil);
  }


  searchForStatistics(timestampFrom: number, timestampUntil: number): void {
    console.log("isSearching");
    this.queryParams = {
      startTime: timestampFrom,
      endTime: timestampUntil,
    };
    this.isLoading = true;
    this.isSearching = true;
    this.responseStatisticsService.searchForStatistics(this.queryParams)
    .pipe(
      expand(() => {
        return this.responseStatisticsService.searchForStatistics(this.queryParams)
      }),
      reduce((acc: FeedbackResponseStatistic[], res: FeedbackResponseStatistics) => acc.concat(res.statistics), [] as FeedbackResponseStatistic[]),
      finalize(() => {
        console.log("hello");
        this.isSearching = false;
        this.hasResult = true;
      }),
    )
    .subscribe((statisticsResults: FeedbackResponseStatistic[]) => this.processStatisticsForGraph(statisticsResults),
      (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private processStatisticsForGraph(stats: FeedbackResponseStatistic[]): void {
    const sourceToFrequencyMap: Map<number, number> = stats
      .reduce((acc: Map<number, number>, stats: FeedbackResponseStatistic) =>
        acc.set(stats.time, (acc.get(stats.time) || 0)),
        new Map<number, number>());
    sourceToFrequencyMap.forEach((value: number, key: number) => {
      this.chartResult.push({ timestamp: key, numberOfTimes: value });
    });
  }

}
