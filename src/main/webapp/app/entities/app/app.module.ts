import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CloudappwatchSharedModule } from 'app/shared/shared.module';
import { AppComponent } from './app.component';
import { AppDetailComponent } from './app-detail.component';
import { AppUpdateComponent } from './app-update.component';
import { AppDeletePopupComponent, AppDeleteDialogComponent } from './app-delete-dialog.component';
import { appRoute, appPopupRoute } from './app.route';

const ENTITY_STATES = [...appRoute, ...appPopupRoute];

@NgModule({
  imports: [CloudappwatchSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [AppComponent, AppDetailComponent, AppUpdateComponent, AppDeleteDialogComponent, AppDeletePopupComponent],
  entryComponents: [AppDeleteDialogComponent]
})
export class CloudappwatchAppModule {}
