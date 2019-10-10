import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { ServiceStatus } from 'app/shared/model/enumerations/service-status.model';

export interface IServer {
  id?: number;
  hostName?: string;
  status?: ServiceStatus;
  lastCheck?: Moment;
  admin?: IUser;
}

export class Server implements IServer {
  constructor(
    public id?: number,
    public hostName?: string,
    public status?: ServiceStatus,
    public lastCheck?: Moment,
    public admin?: IUser
  ) {}
}
