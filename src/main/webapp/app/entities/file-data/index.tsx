import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FileData from './file-data';
import FileDataDetail from './file-data-detail';
import FileDataUpdate from './file-data-update';
import FileDataDeleteDialog from './file-data-delete-dialog';

const FileDataRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FileData />} />
    <Route path="new" element={<FileDataUpdate />} />
    <Route path=":id">
      <Route index element={<FileDataDetail />} />
      <Route path="edit" element={<FileDataUpdate />} />
      <Route path="delete" element={<FileDataDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FileDataRoutes;
