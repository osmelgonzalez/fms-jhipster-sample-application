import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './guardian.reducer';

export const GuardianDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const guardianEntity = useAppSelector(state => state.guardian.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="guardianDetailsHeading">Guardian</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{guardianEntity.id}</dd>
          <dt>
            <span id="firstName">First Name</span>
          </dt>
          <dd>{guardianEntity.firstName}</dd>
          <dt>
            <span id="middleInitial">Middle Initial</span>
          </dt>
          <dd>{guardianEntity.middleInitial}</dd>
          <dt>
            <span id="lastName">Last Name</span>
          </dt>
          <dd>{guardianEntity.lastName}</dd>
          <dt>
            <span id="relationshipToPlayer">Relationship To Player</span>
          </dt>
          <dd>{guardianEntity.relationshipToPlayer}</dd>
          <dt>
            <span id="dateOfBirth">Date Of Birth</span>
          </dt>
          <dd>
            {guardianEntity.dateOfBirth ? (
              <TextFormat value={guardianEntity.dateOfBirth} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="testField">Test Field</span>
          </dt>
          <dd>{guardianEntity.testField}</dd>
          <dt>Players</dt>
          <dd>
            {guardianEntity.players
              ? guardianEntity.players.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {guardianEntity.players && i === guardianEntity.players.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/guardian" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/guardian/${guardianEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default GuardianDetail;
