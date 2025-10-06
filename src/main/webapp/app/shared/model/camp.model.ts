import { IFileData } from 'app/shared/model/file-data.model';
import { CompetitionStatus } from 'app/shared/model/enumerations/competition-status.model';

export interface ICamp {
  id?: number;
  name?: string;
  additionalInfo?: string | null;
  status?: keyof typeof CompetitionStatus;
  image?: IFileData | null;
}

export const defaultValue: Readonly<ICamp> = {};
