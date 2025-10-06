import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Checkin from './checkin';
import CheckinDetail from './checkin-detail';
import CheckinUpdate from './checkin-update';
import CheckinDeleteDialog from './checkin-delete-dialog';

const CheckinRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Checkin />} />
    <Route path="new" element={<CheckinUpdate />} />
    <Route path=":id">
      <Route index element={<CheckinDetail />} />
      <Route path="edit" element={<CheckinUpdate />} />
      <Route path="delete" element={<CheckinDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CheckinRoutes;
