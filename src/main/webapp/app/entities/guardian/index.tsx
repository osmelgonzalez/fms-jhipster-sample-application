import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Guardian from './guardian';
import GuardianDetail from './guardian-detail';
import GuardianUpdate from './guardian-update';
import GuardianDeleteDialog from './guardian-delete-dialog';

const GuardianRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Guardian />} />
    <Route path="new" element={<GuardianUpdate />} />
    <Route path=":id">
      <Route index element={<GuardianDetail />} />
      <Route path="edit" element={<GuardianUpdate />} />
      <Route path="delete" element={<GuardianDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default GuardianRoutes;
