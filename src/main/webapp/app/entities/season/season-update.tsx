import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getFileData } from 'app/entities/file-data/file-data.reducer';
import { getEntities as getOrganizations } from 'app/entities/organization/organization.reducer';
import { CompetitionStatus } from 'app/shared/model/enumerations/competition-status.model';
import { createEntity, getEntity, reset, updateEntity } from './season.reducer';

export const SeasonUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const fileData = useAppSelector(state => state.fileData.entities);
  const organizations = useAppSelector(state => state.organization.entities);
  const seasonEntity = useAppSelector(state => state.season.entity);
  const loading = useAppSelector(state => state.season.loading);
  const updating = useAppSelector(state => state.season.updating);
  const updateSuccess = useAppSelector(state => state.season.updateSuccess);
  const competitionStatusValues = Object.keys(CompetitionStatus);

  const handleClose = () => {
    navigate(`/season${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFileData({}));
    dispatch(getOrganizations({}));
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
    values.start = convertDateTimeToServer(values.start);
    values.ends = convertDateTimeToServer(values.ends);

    const entity = {
      ...seasonEntity,
      ...values,
      image: fileData.find(it => it.id.toString() === values.image?.toString()),
      organization: organizations.find(it => it.id.toString() === values.organization?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          start: displayDefaultDateTime(),
          ends: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVE',
          ...seasonEntity,
          start: convertDateTimeFromServer(seasonEntity.start),
          ends: convertDateTimeFromServer(seasonEntity.ends),
          image: seasonEntity?.image?.id,
          organization: seasonEntity?.organization?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="myApp.season.home.createOrEditLabel" data-cy="SeasonCreateUpdateHeading">
            Create or edit a Season
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="season-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="season-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField
                label="Additional Info"
                id="season-additionalInfo"
                name="additionalInfo"
                data-cy="additionalInfo"
                type="text"
              />
              <ValidatedField label="Status" id="season-status" name="status" data-cy="status" type="select">
                {competitionStatusValues.map(competitionStatus => (
                  <option value={competitionStatus} key={competitionStatus}>
                    {competitionStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Start"
                id="season-start"
                name="start"
                data-cy="start"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Ends"
                id="season-ends"
                name="ends"
                data-cy="ends"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField id="season-image" name="image" data-cy="image" label="Image" type="select">
                <option value="" key="0" />
                {fileData
                  ? fileData.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="season-organization" name="organization" data-cy="organization" label="Organization" type="select">
                <option value="" key="0" />
                {organizations
                  ? organizations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/season" replace color="info">
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

export default SeasonUpdate;
