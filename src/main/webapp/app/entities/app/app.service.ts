import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IApp } from 'app/shared/model/app.model';

type EntityResponseType = HttpResponse<IApp>;
type EntityArrayResponseType = HttpResponse<IApp[]>;

@Injectable({ providedIn: 'root' })
export class AppService {
  public resourceUrl = SERVER_API_URL + 'api/apps';

  constructor(protected http: HttpClient) {}

  create(app: IApp): Observable<EntityResponseType> {
    return this.http.post<IApp>(this.resourceUrl, app, { observe: 'response' });
  }

  update(app: IApp): Observable<EntityResponseType> {
    return this.http.put<IApp>(this.resourceUrl, app, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IApp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApp[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
