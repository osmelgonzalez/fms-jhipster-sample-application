package com.myapp.service.dto;

import com.myapp.domain.enumeration.CompetitionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.myapp.domain.Tournament} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TournamentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    private String additionalInfo;

    @NotNull
    private CompetitionStatus status;

    private Instant start;

    private Instant ends;

    private FileDataDTO image;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TournamentDTO)) {
            return false;
        }

        TournamentDTO tournamentDTO = (TournamentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tournamentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TournamentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", status='" + getStatus() + "'" +
            ", start='" + getStart() + "'" +
            ", ends='" + getEnds() + "'" +
            ", image=" + getImage() +
            "}";
    }
}
