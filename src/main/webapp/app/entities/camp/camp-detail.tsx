import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './camp.reducer';

export const CampDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const campEntity = useAppSelector(state => state.camp.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="campDetailsHeading">Camp</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{campEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{campEntity.name}</dd>
          <dt>
            <span id="additionalInfo">Additional Info</span>
          </dt>
          <dd>{campEntity.additionalInfo}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{campEntity.status}</dd>
          <dt>Image</dt>
          <dd>{campEntity.image ? campEntity.image.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/camp" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/camp/${campEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CampDetail;
