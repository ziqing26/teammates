import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  ActionClasses,
  QueryStatisticsParams,
  ResponseStatistics,
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

  searchForStatistics(queryParams: Partial<QueryStatisticsParams>): Observable<ResponseStatistics> {
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
