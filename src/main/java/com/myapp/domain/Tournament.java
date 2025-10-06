package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myapp.domain.enumeration.CompetitionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tournament.
 */
@Entity
@Table(name = "tournament")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tournament implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "additional_info")
    private String additionalInfo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompetitionStatus status;

    @Column(name = "start")
    private Instant start;

    @Column(name = "ends")
    private Instant ends;

    @JsonIgnoreProperties(value = { "tournament", "season", "camp" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private FileData image;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tournament id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tournament name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public Tournament additionalInfo(String additionalInfo) {
        this.setAdditionalInfo(additionalInfo);
        return this;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public CompetitionStatus getStatus() {
        return this.status;
    }

    public Tournament status(CompetitionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    public Instant getStart() {
        return this.start;
    }

    public Tournament start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnds() {
        return this.ends;
    }

    public Tournament ends(Instant ends) {
        this.setEnds(ends);
        return this;
    }

    public void setEnds(Instant ends) {
        this.ends = ends;
    }

    public FileData getImage() {
        return this.image;
    }

    public void setImage(FileData fileData) {
        this.image = fileData;
    }

    public Tournament image(FileData fileData) {
        this.setImage(fileData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tournament)) {
            return false;
        }
        return getId() != null && getId().equals(((Tournament) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tournament{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", status='" + getStatus() + "'" +
            ", start='" + getStart() + "'" +
            ", ends='" + getEnds() + "'" +
            "}";
    }
}
