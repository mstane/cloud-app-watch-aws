import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IServer } from 'app/shared/model/server.model';

type EntityResponseType = HttpResponse<IServer>;
type EntityArrayResponseType = HttpResponse<IServer[]>;

@Injectable({ providedIn: 'root' })
export class ServerService {
  public resourceUrl = SERVER_API_URL + 'api/servers';

  constructor(protected http: HttpClient) {}

  create(server: IServer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(server);
    return this.http
      .post<IServer>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(server: IServer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(server);
    return this.http
      .put<IServer>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IServer>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IServer[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(server: IServer): IServer {
    const copy: IServer = Object.assign({}, server, {
      lastCheck: server.lastCheck != null && server.lastCheck.isValid() ? server.lastCheck.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lastCheck = res.body.lastCheck != null ? moment(res.body.lastCheck) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((server: IServer) => {
        server.lastCheck = server.lastCheck != null ? moment(server.lastCheck) : null;
      });
    }
    return res;
  }
}
