import tournament from 'app/entities/tournament/tournament.reducer';
import team from 'app/entities/team/team.reducer';
import season from 'app/entities/season/season.reducer';
import player from 'app/entities/player/player.reducer';
import organization from 'app/entities/organization/organization.reducer';
import guardian from 'app/entities/guardian/guardian.reducer';
import checkin from 'app/entities/checkin/checkin.reducer';
import camp from 'app/entities/camp/camp.reducer';
import fileData from 'app/entities/file-data/file-data.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  tournament,
  team,
  season,
  player,
  organization,
  guardian,
  checkin,
  camp,
  fileData,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
