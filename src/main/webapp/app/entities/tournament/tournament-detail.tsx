import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tournament.reducer';

export const TournamentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tournamentEntity = useAppSelector(state => state.tournament.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tournamentDetailsHeading">Tournament</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{tournamentEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{tournamentEntity.name}</dd>
          <dt>
            <span id="additionalInfo">Additional Info</span>
          </dt>
          <dd>{tournamentEntity.additionalInfo}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{tournamentEntity.status}</dd>
          <dt>
            <span id="start">Start</span>
          </dt>
          <dd>{tournamentEntity.start ? <TextFormat value={tournamentEntity.start} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="ends">Ends</span>
          </dt>
          <dd>{tournamentEntity.ends ? <TextFormat value={tournamentEntity.ends} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Image</dt>
          <dd>{tournamentEntity.image ? tournamentEntity.image.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/tournament" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tournament/${tournamentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TournamentDetail;
