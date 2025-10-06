package com.myapp.service.dto;

import com.myapp.domain.enumeration.CompetitionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.myapp.domain.Season} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeasonDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    private String additionalInfo;

    @NotNull
    private CompetitionStatus status;

    @NotNull
    private Instant start;

    @NotNull
    private Instant ends;

    private FileDataDTO image;

    private OrganizationDTO organization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnds() {
        return ends;
    }

    public void setEnds(Instant ends) {
        this.ends = ends;
    }

    public FileDataDTO getImage() {
        return image;
    }

    public void setImage(FileDataDTO image) {
        this.image = image;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeasonDTO)) {
            return false;
        }

        SeasonDTO seasonDTO = (SeasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, seasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeasonDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", status='" + getStatus() + "'" +
            ", start='" + getStart() + "'" +
            ", ends='" + getEnds() + "'" +
            ", image=" + getImage() +
            ", organization=" + getOrganization() +
            "}";
    }
}
