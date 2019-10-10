import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IServer, Server } from 'app/shared/model/server.model';
import { ServerService } from './server.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-server-update',
  templateUrl: './server-update.component.html'
})
export class ServerUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    hostName: [],
    status: [],
    lastCheck: [],
    admin: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected serverService: ServerService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ server }) => {
      this.updateForm(server);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(server: IServer) {
    this.editForm.patchValue({
      id: server.id,
      hostName: server.hostName,
      status: server.status,
      lastCheck: server.lastCheck != null ? server.lastCheck.format(DATE_TIME_FORMAT) : null,
      admin: server.admin
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const server = this.createFromForm();
    if (server.id !== undefined) {
      this.subscribeToSaveResponse(this.serverService.update(server));
    } else {
      this.subscribeToSaveResponse(this.serverService.create(server));
    }
  }

  private createFromForm(): IServer {
    return {
      ...new Server(),
      id: this.editForm.get(['id']).value,
      hostName: this.editForm.get(['hostName']).value,
      status: this.editForm.get(['status']).value,
      lastCheck:
        this.editForm.get(['lastCheck']).value != null ? moment(this.editForm.get(['lastCheck']).value, DATE_TIME_FORMAT) : undefined,
      admin: this.editForm.get(['admin']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServer>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }
}
