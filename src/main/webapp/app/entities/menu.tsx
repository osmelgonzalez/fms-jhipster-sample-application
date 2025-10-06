import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/tournament">
        Tournament
      </MenuItem>
      <MenuItem icon="asterisk" to="/team">
        Team
      </MenuItem>
      <MenuItem icon="asterisk" to="/season">
        Season
      </MenuItem>
      <MenuItem icon="asterisk" to="/player">
        Player
      </MenuItem>
      <MenuItem icon="asterisk" to="/organization">
        Organization
      </MenuItem>
      <MenuItem icon="asterisk" to="/guardian">
        Guardian
      </MenuItem>
      <MenuItem icon="asterisk" to="/checkin">
        Checkin
      </MenuItem>
      <MenuItem icon="asterisk" to="/camp">
        Camp
      </MenuItem>
      <MenuItem icon="asterisk" to="/file-data">
        File Data
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
