import dayjs from 'dayjs';
import { IFileData } from 'app/shared/model/file-data.model';
import { IOrganization } from 'app/shared/model/organization.model';
import { CompetitionStatus } from 'app/shared/model/enumerations/competition-status.model';

export interface ISeason {
  id?: number;
  name?: string;
  additionalInfo?: string | null;
  status?: keyof typeof CompetitionStatus;
  start?: dayjs.Dayjs;
  ends?: dayjs.Dayjs;
  image?: IFileData | null;
  organization?: IOrganization | null;
}

export const defaultValue: Readonly<ISeason> = {};
