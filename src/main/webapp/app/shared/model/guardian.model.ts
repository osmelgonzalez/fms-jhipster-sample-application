import { IPlayer } from 'app/shared/model/player.model';

export interface IGuardian {
  id?: number;
  firstName?: string;
  middleInitial?: string | null;
  lastName?: string;
  relationshipToPlayer?: string;
  players?: IPlayer[] | null;
}

export const defaultValue: Readonly<IGuardian> = {};
