import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AdminStatisticsPageComponent } from './admin-statistics-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminStatisticsPageComponent,
  },
];

/**
 * Module for admin timezone page.
 */
@NgModule({
  declarations: [
    AdminStatisticsPageComponent,
  ],
  exports: [
    AdminStatisticsPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    NgbDatepickerModule,
    NgbTimepickerModule,
    LoadingSpinnerModule,
  ],
})
export class AdminStatisticsPageModule { }
