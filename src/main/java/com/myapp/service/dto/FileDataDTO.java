package com.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.myapp.domain.FileData} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String uid;

    @NotNull
    @Size(max = 255)
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileDataDTO)) {
            return false;
        }

        FileDataDTO fileDataDTO = (FileDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fileDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileDataDTO{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", fileName='" + getFileName() + "'" +
            "}";
    }
}
