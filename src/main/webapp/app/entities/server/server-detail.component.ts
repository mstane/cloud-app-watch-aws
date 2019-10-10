import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IServer } from 'app/shared/model/server.model';

@Component({
  selector: 'jhi-server-detail',
  templateUrl: './server-detail.component.html'
})
export class ServerDetailComponent implements OnInit {
  server: IServer;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ server }) => {
      this.server = server;
    });
  }

  previousState() {
    window.history.back();
  }
}
