import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './player.reducer';

export const PlayerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const playerEntity = useAppSelector(state => state.player.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="playerDetailsHeading">Player</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{playerEntity.id}</dd>
          <dt>
            <span id="firstName">First Name</span>
          </dt>
          <dd>{playerEntity.firstName}</dd>
          <dt>
            <span id="middleInitial">Middle Initial</span>
          </dt>
          <dd>{playerEntity.middleInitial}</dd>
          <dt>
            <span id="lastName">Last Name</span>
          </dt>
          <dd>{playerEntity.lastName}</dd>
          <dt>
            <span id="gender">Gender</span>
          </dt>
          <dd>{playerEntity.gender}</dd>
          <dt>
            <span id="dateOfBirth">Date Of Birth</span>
          </dt>
          <dd>
            {playerEntity.dateOfBirth ? <TextFormat value={playerEntity.dateOfBirth} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>Guardians</dt>
          <dd>
            {playerEntity.guardians
              ? playerEntity.guardians.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {playerEntity.guardians && i === playerEntity.guardians.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Teams</dt>
          <dd>
            {playerEntity.teams
              ? playerEntity.teams.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {playerEntity.teams && i === playerEntity.teams.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/player" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/player/${playerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default PlayerDetail;
