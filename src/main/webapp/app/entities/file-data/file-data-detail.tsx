import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './file-data.reducer';

export const FileDataDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const fileDataEntity = useAppSelector(state => state.fileData.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fileDataDetailsHeading">File Data</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{fileDataEntity.id}</dd>
          <dt>
            <span id="uid">Uid</span>
          </dt>
          <dd>{fileDataEntity.uid}</dd>
          <dt>
            <span id="fileName">File Name</span>
          </dt>
          <dd>{fileDataEntity.fileName}</dd>
        </dl>
        <Button tag={Link} to="/file-data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/file-data/${fileDataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FileDataDetail;
