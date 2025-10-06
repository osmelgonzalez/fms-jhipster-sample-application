import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './checkin.reducer';

export const CheckinDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const checkinEntity = useAppSelector(state => state.checkin.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="checkinDetailsHeading">Checkin</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{checkinEntity.id}</dd>
          <dt>
            <span id="timestamp">Timestamp</span>
          </dt>
          <dd>{checkinEntity.timestamp ? <TextFormat value={checkinEntity.timestamp} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Player</dt>
          <dd>{checkinEntity.player ? checkinEntity.player.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/checkin" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/checkin/${checkinEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CheckinDetail;
