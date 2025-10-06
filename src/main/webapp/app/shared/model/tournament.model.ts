import dayjs from 'dayjs';
import { IFileData } from 'app/shared/model/file-data.model';
import { CompetitionStatus } from 'app/shared/model/enumerations/competition-status.model';

export interface ITournament {
  id?: number;
  name?: string;
  additionalInfo?: string | null;
  status?: keyof typeof CompetitionStatus;
  start?: dayjs.Dayjs | null;
  ends?: dayjs.Dayjs | null;
  image?: IFileData | null;
}

export const defaultValue: Readonly<ITournament> = {};
