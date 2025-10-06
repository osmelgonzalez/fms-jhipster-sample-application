import dayjs from 'dayjs';
import { IPlayer } from 'app/shared/model/player.model';

export interface ICheckin {
  id?: number;
  timestamp?: dayjs.Dayjs;
  player?: IPlayer;
}

export const defaultValue: Readonly<ICheckin> = {};
