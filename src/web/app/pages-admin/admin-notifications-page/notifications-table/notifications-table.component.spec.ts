import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import moment from 'moment-timezone';
import { NotificationStyle, NotificationTargetUser } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { AdminNotificationsPageModule } from '../admin-notifications-page.module';
import { NotificationsTableRowModel } from './notifications-table-model';
import { NotificationsTableComponent } from './notifications-table.component';

const notificationTableRowModel1: NotificationsTableRowModel = {
  isHighlighted: true,
  notification: {
    notificationId: 'notification1',
    startTimestamp: moment('2017-09-15 09:30:00').valueOf(),
    endTimestamp: moment('2050-09-15 09:30:00').valueOf(),
    createdAt: moment('2017-09-15 09:30:00').valueOf(),
    style: NotificationStyle.SUCCESS,
    targetUser: NotificationTargetUser.INSTRUCTOR,
    title: 'valid title 1',
    message: 'valid message 1',
    shown: false,
  },
};

const notificationTableRowModel2: NotificationsTableRowModel = {
  isHighlighted: false,
  notification: {
    notificationId: 'notification2',
    startTimestamp: moment('2018-12-15 09:30:00').valueOf(),
    endTimestamp: moment('2050-09-15 09:30:00').valueOf(),
    createdAt: moment('2018-11-15 09:30:00').valueOf(),
    style: NotificationStyle.DANGER,
    targetUser: NotificationTargetUser.GENERAL,
    title: 'valid title 2',
    message: 'valid message 2',
    shown: false,
  },
};

describe('NotificationsTableComponent', () => {
  let component: NotificationsTableComponent;
  let fixture: ComponentFixture<NotificationsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AdminNotificationsPageModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in notification page with 2 notifications sorted by create time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
    component.notificationsTableRowModelsSortOrder = SortOrder.DESC;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in notification page with 2 notifications sorted by start time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_START_TIME;
    component.notificationsTableRowModelsSortOrder = SortOrder.DESC;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
