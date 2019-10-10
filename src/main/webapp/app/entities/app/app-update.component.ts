import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IApp, App } from 'app/shared/model/app.model';
import { AppService } from './app.service';
import { IServer } from 'app/shared/model/server.model';
import { ServerService } from 'app/entities/server/server.service';

@Component({
  selector: 'jhi-app-update',
  templateUrl: './app-update.component.html'
})
export class AppUpdateComponent implements OnInit {
  isSaving: boolean;

  servers: IServer[];

  editForm = this.fb.group({
    id: [],
    commandLine: [],
    serviceFlag: [],
    status: [],
    server: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected appService: AppService,
    protected serverService: ServerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ app }) => {
      this.updateForm(app);
    });
    this.serverService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IServer[]>) => mayBeOk.ok),
        map((response: HttpResponse<IServer[]>) => response.body)
      )
      .subscribe((res: IServer[]) => (this.servers = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(app: IApp) {
    this.editForm.patchValue({
      id: app.id,
      commandLine: app.commandLine,
      serviceFlag: app.serviceFlag,
      status: app.status,
      server: app.server
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const app = this.createFromForm();
    if (app.id !== undefined) {
      this.subscribeToSaveResponse(this.appService.update(app));
    } else {
      this.subscribeToSaveResponse(this.appService.create(app));
    }
  }

  private createFromForm(): IApp {
    return {
      ...new App(),
      id: this.editForm.get(['id']).value,
      commandLine: this.editForm.get(['commandLine']).value,
      serviceFlag: this.editForm.get(['serviceFlag']).value,
      status: this.editForm.get(['status']).value,
      server: this.editForm.get(['server']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApp>>) {
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

  trackServerById(index: number, item: IServer) {
    return item.id;
  }
}
