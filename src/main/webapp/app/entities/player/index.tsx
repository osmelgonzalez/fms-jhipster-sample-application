import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Player from './player';
import PlayerDetail from './player-detail';
import PlayerUpdate from './player-update';
import PlayerDeleteDialog from './player-delete-dialog';

const PlayerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Player />} />
    <Route path="new" element={<PlayerUpdate />} />
    <Route path=":id">
      <Route index element={<PlayerDetail />} />
      <Route path="edit" element={<PlayerUpdate />} />
      <Route path="delete" element={<PlayerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PlayerRoutes;
