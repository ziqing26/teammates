import { TestBed } from '@angular/core/testing';

import { ResponseStatisticsService } from './response-statistics.service';

describe('ResponseStatisticsService', () => {
  let service: ResponseStatisticsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResponseStatisticsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
