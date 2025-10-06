import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getFileData } from 'app/entities/file-data/file-data.reducer';
import { CompetitionStatus } from 'app/shared/model/enumerations/competition-status.model';
import { createEntity, getEntity, reset, updateEntity } from './camp.reducer';

export const CampUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const fileData = useAppSelector(state => state.fileData.entities);
  const campEntity = useAppSelector(state => state.camp.entity);
  const loading = useAppSelector(state => state.camp.loading);
  const updating = useAppSelector(state => state.camp.updating);
  const updateSuccess = useAppSelector(state => state.camp.updateSuccess);
  const competitionStatusValues = Object.keys(CompetitionStatus);

  const handleClose = () => {
    navigate(`/camp${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFileData({}));
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
      ...campEntity,
      ...values,
      image: fileData.find(it => it.id.toString() === values.image?.toString()),
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
          status: 'ACTIVE',
          ...campEntity,
          image: campEntity?.image?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="myApp.camp.home.createOrEditLabel" data-cy="CampCreateUpdateHeading">
            Create or edit a Camp
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="camp-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="camp-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField label="Additional Info" id="camp-additionalInfo" name="additionalInfo" data-cy="additionalInfo" type="text" />
              <ValidatedField label="Status" id="camp-status" name="status" data-cy="status" type="select">
                {competitionStatusValues.map(competitionStatus => (
                  <option value={competitionStatus} key={competitionStatus}>
                    {competitionStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="camp-image" name="image" data-cy="image" label="Image" type="select">
                <option value="" key="0" />
                {fileData
                  ? fileData.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/camp" replace color="info">
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

export default CampUpdate;
