import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IServer } from 'app/shared/model/server.model';
import { ServerService } from './server.service';

@Component({
  selector: 'jhi-server-delete-dialog',
  templateUrl: './server-delete-dialog.component.html'
})
export class ServerDeleteDialogComponent {
  server: IServer;

  constructor(protected serverService: ServerService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.serverService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'serverListModification',
        content: 'Deleted an server'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-server-delete-popup',
  template: ''
})
export class ServerDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ server }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ServerDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.server = server;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/server', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/server', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
