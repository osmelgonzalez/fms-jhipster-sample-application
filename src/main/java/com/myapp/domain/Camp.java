package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myapp.domain.enumeration.CompetitionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Camp.
 */
@Entity
@Table(name = "camp")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Camp implements Serializable {

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

    @JsonIgnoreProperties(value = { "tournament", "season", "camp" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private FileData image;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Camp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Camp name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public Camp additionalInfo(String additionalInfo) {
        this.setAdditionalInfo(additionalInfo);
        return this;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public CompetitionStatus getStatus() {
        return this.status;
    }

    public Camp status(CompetitionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    public FileData getImage() {
        return this.image;
    }

    public void setImage(FileData fileData) {
        this.image = fileData;
    }

    public Camp image(FileData fileData) {
        this.setImage(fileData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Camp)) {
            return false;
        }
        return getId() != null && getId().equals(((Camp) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Camp{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
