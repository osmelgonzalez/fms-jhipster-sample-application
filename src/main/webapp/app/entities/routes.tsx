import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tournament from './tournament';
import Team from './team';
import Season from './season';
import Player from './player';
import Organization from './organization';
import Guardian from './guardian';
import Checkin from './checkin';
import Camp from './camp';
import FileData from './file-data';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="tournament/*" element={<Tournament />} />
        <Route path="team/*" element={<Team />} />
        <Route path="season/*" element={<Season />} />
        <Route path="player/*" element={<Player />} />
        <Route path="organization/*" element={<Organization />} />
        <Route path="guardian/*" element={<Guardian />} />
        <Route path="checkin/*" element={<Checkin />} />
        <Route path="camp/*" element={<Camp />} />
        <Route path="file-data/*" element={<FileData />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
