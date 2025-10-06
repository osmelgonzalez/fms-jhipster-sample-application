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
 * A Season.
 */
@Entity
@Table(name = "season")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Season implements Serializable {

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

    @NotNull
    @Column(name = "start", nullable = false)
    private Instant start;

    @NotNull
    @Column(name = "ends", nullable = false)
    private Instant ends;

    @JsonIgnoreProperties(value = { "tournament", "season", "camp" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private FileData image;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Season id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Season name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public Season additionalInfo(String additionalInfo) {
        this.setAdditionalInfo(additionalInfo);
        return this;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public CompetitionStatus getStatus() {
        return this.status;
    }

    public Season status(CompetitionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    public Instant getStart() {
        return this.start;
    }

    public Season start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnds() {
        return this.ends;
    }

    public Season ends(Instant ends) {
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

    public Season image(FileData fileData) {
        this.setImage(fileData);
        return this;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Season organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Season)) {
            return false;
        }
        return getId() != null && getId().equals(((Season) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Season{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", status='" + getStatus() + "'" +
            ", start='" + getStart() + "'" +
            ", ends='" + getEnds() + "'" +
            "}";
    }
}
