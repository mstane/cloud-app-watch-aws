import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Server } from 'app/shared/model/server.model';
import { ServerService } from './server.service';
import { ServerComponent } from './server.component';
import { ServerDetailComponent } from './server-detail.component';
import { ServerUpdateComponent } from './server-update.component';
import { ServerDeletePopupComponent } from './server-delete-dialog.component';
import { IServer } from 'app/shared/model/server.model';

@Injectable({ providedIn: 'root' })
export class ServerResolve implements Resolve<IServer> {
  constructor(private service: ServerService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IServer> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Server>) => response.ok),
        map((server: HttpResponse<Server>) => server.body)
      );
    }
    return of(new Server());
  }
}

export const serverRoute: Routes = [
  {
    path: '',
    component: ServerComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'cloudappwatchApp.server.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ServerDetailComponent,
    resolve: {
      server: ServerResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'cloudappwatchApp.server.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ServerUpdateComponent,
    resolve: {
      server: ServerResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'cloudappwatchApp.server.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ServerUpdateComponent,
    resolve: {
      server: ServerResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'cloudappwatchApp.server.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const serverPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ServerDeletePopupComponent,
    resolve: {
      server: ServerResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'cloudappwatchApp.server.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
