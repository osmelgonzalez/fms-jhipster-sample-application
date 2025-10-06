import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tournament from './tournament';
import TournamentDetail from './tournament-detail';
import TournamentUpdate from './tournament-update';
import TournamentDeleteDialog from './tournament-delete-dialog';

const TournamentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Tournament />} />
    <Route path="new" element={<TournamentUpdate />} />
    <Route path=":id">
      <Route index element={<TournamentDetail />} />
      <Route path="edit" element={<TournamentUpdate />} />
      <Route path="delete" element={<TournamentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TournamentRoutes;
