package com.myapp.web.rest;

import static com.myapp.domain.FileDataAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.FileData;
import com.myapp.repository.FileDataRepository;
import com.myapp.service.dto.FileDataDTO;
import com.myapp.service.mapper.FileDataMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FileDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FileDataResourceIT {

    private static final String DEFAULT_UID = "AAAAAAAAAA";
    private static final String UPDATED_UID = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/file-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FileDataRepository fileDataRepository;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFileDataMockMvc;

    private FileData fileData;

    private FileData insertedFileData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileData createEntity() {
        return new FileData().uid(DEFAULT_UID).fileName(DEFAULT_FILE_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileData createUpdatedEntity() {
        return new FileData().uid(UPDATED_UID).fileName(UPDATED_FILE_NAME);
    }

    @BeforeEach
    void initTest() {
        fileData = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFileData != null) {
            fileDataRepository.delete(insertedFileData);
            insertedFileData = null;
        }
    }

    @Test
    @Transactional
    void createFileData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);
        var returnedFileDataDTO = om.readValue(
            restFileDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileDataDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FileDataDTO.class
        );

        // Validate the FileData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFileData = fileDataMapper.toEntity(returnedFileDataDTO);
        assertFileDataUpdatableFieldsEquals(returnedFileData, getPersistedFileData(returnedFileData));

        insertedFileData = returnedFileData;
    }

    @Test
    @Transactional
    void createFileDataWithExistingId() throws Exception {
        // Create the FileData with an existing ID
        fileData.setId(1L);
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileData.setUid(null);

        // Create the FileData, which fails.
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        restFileDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileData.setFileName(null);

        // Create the FileData, which fails.
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        restFileDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFileData() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        // Get all the fileDataList
        restFileDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileData.getId().intValue())))
            .andExpect(jsonPath("$.[*].uid").value(hasItem(DEFAULT_UID)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));
    }

    @Test
    @Transactional
    void getFileData() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        // Get the fileData
        restFileDataMockMvc
            .perform(get(ENTITY_API_URL_ID, fileData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fileData.getId().intValue()))
            .andExpect(jsonPath("$.uid").value(DEFAULT_UID))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME));
    }

    @Test
    @Transactional
    void getNonExistingFileData() throws Exception {
        // Get the fileData
        restFileDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFileData() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileData
        FileData updatedFileData = fileDataRepository.findById(fileData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFileData are not directly saved in db
        em.detach(updatedFileData);
        updatedFileData.uid(UPDATED_UID).fileName(UPDATED_FILE_NAME);
        FileDataDTO fileDataDTO = fileDataMapper.toDto(updatedFileData);

        restFileDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFileDataToMatchAllProperties(updatedFileData);
    }

    @Test
    @Transactional
    void putNonExistingFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFileDataWithPatch() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileData using partial update
        FileData partialUpdatedFileData = new FileData();
        partialUpdatedFileData.setId(fileData.getId());

        partialUpdatedFileData.uid(UPDATED_UID);

        restFileDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileData))
            )
            .andExpect(status().isOk());

        // Validate the FileData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileDataUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFileData, fileData), getPersistedFileData(fileData));
    }

    @Test
    @Transactional
    void fullUpdateFileDataWithPatch() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileData using partial update
        FileData partialUpdatedFileData = new FileData();
        partialUpdatedFileData.setId(fileData.getId());

        partialUpdatedFileData.uid(UPDATED_UID).fileName(UPDATED_FILE_NAME);

        restFileDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileData))
            )
            .andExpect(status().isOk());

        // Validate the FileData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileDataUpdatableFieldsEquals(partialUpdatedFileData, getPersistedFileData(partialUpdatedFileData));
    }

    @Test
    @Transactional
    void patchNonExistingFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fileDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFileData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileData.setId(longCount.incrementAndGet());

        // Create the FileData
        FileDataDTO fileDataDTO = fileDataMapper.toDto(fileData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fileDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFileData() throws Exception {
        // Initialize the database
        insertedFileData = fileDataRepository.saveAndFlush(fileData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fileData
        restFileDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, fileData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fileDataRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected FileData getPersistedFileData(FileData fileData) {
        return fileDataRepository.findById(fileData.getId()).orElseThrow();
    }

    protected void assertPersistedFileDataToMatchAllProperties(FileData expectedFileData) {
        assertFileDataAllPropertiesEquals(expectedFileData, getPersistedFileData(expectedFileData));
    }

    protected void assertPersistedFileDataToMatchUpdatableProperties(FileData expectedFileData) {
        assertFileDataAllUpdatablePropertiesEquals(expectedFileData, getPersistedFileData(expectedFileData));
    }
}
