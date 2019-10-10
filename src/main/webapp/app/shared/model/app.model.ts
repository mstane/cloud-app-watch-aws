import { IServer } from 'app/shared/model/server.model';
import { AppStatus } from 'app/shared/model/enumerations/app-status.model';

export interface IApp {
  id?: number;
  commandLine?: any;
  serviceFlag?: boolean;
  status?: AppStatus;
  server?: IServer;
}

export class App implements IApp {
  constructor(
    public id?: number,
    public commandLine?: any,
    public serviceFlag?: boolean,
    public status?: AppStatus,
    public server?: IServer
  ) {
    this.serviceFlag = this.serviceFlag || false;
  }
}
