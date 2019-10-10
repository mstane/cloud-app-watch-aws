import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'server',
        loadChildren: () => import('./server/server.module').then(m => m.CloudappwatchServerModule)
      },
      {
        path: 'app',
        loadChildren: () => import('./app/app.module').then(m => m.CloudappwatchAppModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class CloudappwatchEntityModule {}
