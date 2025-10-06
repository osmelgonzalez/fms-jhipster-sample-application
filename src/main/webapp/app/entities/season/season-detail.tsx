import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './season.reducer';

export const SeasonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const seasonEntity = useAppSelector(state => state.season.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="seasonDetailsHeading">Season</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{seasonEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{seasonEntity.name}</dd>
          <dt>
            <span id="additionalInfo">Additional Info</span>
          </dt>
          <dd>{seasonEntity.additionalInfo}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{seasonEntity.status}</dd>
          <dt>
            <span id="start">Start</span>
          </dt>
          <dd>{seasonEntity.start ? <TextFormat value={seasonEntity.start} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="ends">Ends</span>
          </dt>
          <dd>{seasonEntity.ends ? <TextFormat value={seasonEntity.ends} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Image</dt>
          <dd>{seasonEntity.image ? seasonEntity.image.id : ''}</dd>
          <dt>Organization</dt>
          <dd>{seasonEntity.organization ? seasonEntity.organization.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/season" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/season/${seasonEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SeasonDetail;
