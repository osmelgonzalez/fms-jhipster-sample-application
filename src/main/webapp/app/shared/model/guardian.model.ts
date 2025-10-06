import dayjs from 'dayjs';
import { IPlayer } from 'app/shared/model/player.model';

export interface IGuardian {
  id?: number;
  firstName?: string;
  middleInitial?: string | null;
  lastName?: string;
  relationshipToPlayer?: string;
  dateOfBirth?: dayjs.Dayjs;
  testField?: string | null;
  players?: IPlayer[] | null;
}

export const defaultValue: Readonly<IGuardian> = {};
