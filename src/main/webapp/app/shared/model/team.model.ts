import { IPlayer } from 'app/shared/model/player.model';

export interface ITeam {
  id?: number;
  name?: string | null;
  players?: IPlayer[] | null;
}

export const defaultValue: Readonly<ITeam> = {};
