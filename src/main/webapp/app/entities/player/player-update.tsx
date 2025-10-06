import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getGuardians } from 'app/entities/guardian/guardian.reducer';
import { getEntities as getTeams } from 'app/entities/team/team.reducer';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { createEntity, getEntity, reset, updateEntity } from './player.reducer';

export const PlayerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const guardians = useAppSelector(state => state.guardian.entities);
  const teams = useAppSelector(state => state.team.entities);
  const playerEntity = useAppSelector(state => state.player.entity);
  const loading = useAppSelector(state => state.player.loading);
  const updating = useAppSelector(state => state.player.updating);
  const updateSuccess = useAppSelector(state => state.player.updateSuccess);
  const genderValues = Object.keys(Gender);

  const handleClose = () => {
    navigate(`/player${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getGuardians({}));
    dispatch(getTeams({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...playerEntity,
      ...values,
      guardians: mapIdList(values.guardians),
      teams: mapIdList(values.teams),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          gender: 'MALE',
          ...playerEntity,
          guardians: playerEntity?.guardians?.map(e => e.id.toString()),
          teams: playerEntity?.teams?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="myApp.player.home.createOrEditLabel" data-cy="PlayerCreateUpdateHeading">
            Create or edit a Player
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="player-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="First Name"
                id="player-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField
                label="Middle Initial"
                id="player-middleInitial"
                name="middleInitial"
                data-cy="middleInitial"
                type="text"
                validate={{
                  maxLength: { value: 1, message: 'This field cannot be longer than 1 characters.' },
                }}
              />
              <ValidatedField
                label="Last Name"
                id="player-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField label="Gender" id="player-gender" name="gender" data-cy="gender" type="select">
                {genderValues.map(gender => (
                  <option value={gender} key={gender}>
                    {gender}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Date Of Birth" id="player-dateOfBirth" name="dateOfBirth" data-cy="dateOfBirth" type="date" />
              <ValidatedField label="Guardians" id="player-guardians" data-cy="guardians" type="select" multiple name="guardians">
                <option value="" key="0" />
                {guardians
                  ? guardians.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Teams" id="player-teams" data-cy="teams" type="select" multiple name="teams">
                <option value="" key="0" />
                {teams
                  ? teams.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/player" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PlayerUpdate;
