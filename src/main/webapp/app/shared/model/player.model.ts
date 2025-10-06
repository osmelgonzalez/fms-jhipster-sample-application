import dayjs from 'dayjs';
import { IGuardian } from 'app/shared/model/guardian.model';
import { ITeam } from 'app/shared/model/team.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';

export interface IPlayer {
  id?: number;
  firstName?: string;
  middleInitial?: string | null;
  lastName?: string;
  gender?: keyof typeof Gender | null;
  dateOfBirth?: dayjs.Dayjs | null;
  guardians?: IGuardian[] | null;
  teams?: ITeam[] | null;
}

export const defaultValue: Readonly<IPlayer> = {};
