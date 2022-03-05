import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  ActionClasses,
  FeedbackResponseStatistics,
  QueryLogsParams,
} from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles response statistics provision.
 */
@Injectable({
  providedIn: 'root',
})
export class ResponseStatisticsService {

  constructor(private httpRequestService: HttpRequestService) { }

  searchForStatistics(queryParams: Partial<QueryLogsParams>): Observable<FeedbackResponseStatistics> {
    const paramMap: Record<string, string> = {
      frsstarttime: `${queryParams.startTime || -1}`,
      frsendtime: `${queryParams.endTime || -1}`,
    };
    return this.httpRequestService.get(ResourceEndpoints.RESPONSE_STATISTICS, paramMap);
  }

  getActionClassList(): Observable<ActionClasses> {
    return this.httpRequestService.get(ResourceEndpoints.ACTION_CLASS);
  }
}
