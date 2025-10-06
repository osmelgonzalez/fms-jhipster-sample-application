import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Camp from './camp';
import CampDetail from './camp-detail';
import CampUpdate from './camp-update';
import CampDeleteDialog from './camp-delete-dialog';

const CampRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Camp />} />
    <Route path="new" element={<CampUpdate />} />
    <Route path=":id">
      <Route index element={<CampDetail />} />
      <Route path="edit" element={<CampUpdate />} />
      <Route path="delete" element={<CampDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CampRoutes;
