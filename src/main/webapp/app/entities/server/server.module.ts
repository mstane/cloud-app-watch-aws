import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CloudappwatchSharedModule } from 'app/shared/shared.module';
import { ServerComponent } from './server.component';
import { ServerDetailComponent } from './server-detail.component';
import { ServerUpdateComponent } from './server-update.component';
import { ServerDeletePopupComponent, ServerDeleteDialogComponent } from './server-delete-dialog.component';
import { serverRoute, serverPopupRoute } from './server.route';

const ENTITY_STATES = [...serverRoute, ...serverPopupRoute];

@NgModule({
  imports: [CloudappwatchSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [ServerComponent, ServerDetailComponent, ServerUpdateComponent, ServerDeleteDialogComponent, ServerDeletePopupComponent],
  entryComponents: [ServerDeleteDialogComponent]
})
export class CloudappwatchServerModule {}
