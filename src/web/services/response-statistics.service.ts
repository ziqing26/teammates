import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  ActionClasses,
  QueryLogsParams,
  FeedbackResponseStatistics,
} from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles response statistics provision.
 */
@Injectable({
  providedIn: 'root'
})
export class ResponseStatisticsService {

  constructor(private httpRequestService: HttpRequestService) { }

  searchForStatistics(queryParams: Partial<QueryLogsParams>): Observable<FeedbackResponseStatistics> {
    const paramMap: Record<string, string> = {
      fssstarttime: `${queryParams.startTime || -1}`,
      fssendtime: `${queryParams.endTime || -1}`,
    };
    return this.httpRequestService.get(ResourceEndpoints.RESPONSE_STATISTICS, paramMap);
  }

  getActionClassList(): Observable<ActionClasses> {
    return this.httpRequestService.get(ResourceEndpoints.ACTION_CLASS);
  }
}
