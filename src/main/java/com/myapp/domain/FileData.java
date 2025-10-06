package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FileData.
 */
@Entity
@Table(name = "file_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "uid", length = 255, nullable = false)
    private String uid;

    @NotNull
    @Size(max = 255)
    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @JsonIgnoreProperties(value = { "image" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "image")
    private Tournament tournament;

    @JsonIgnoreProperties(value = { "image", "organization" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "image")
    private Season season;

    @JsonIgnoreProperties(value = { "image" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "image")
    private Camp camp;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FileData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public FileData uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFileName() {
        return this.fileName;
    }

    public FileData fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Tournament getTournament() {
        return this.tournament;
    }

    public void setTournament(Tournament tournament) {
        if (this.tournament != null) {
            this.tournament.setImage(null);
        }
        if (tournament != null) {
            tournament.setImage(this);
        }
        this.tournament = tournament;
    }

    public FileData tournament(Tournament tournament) {
        this.setTournament(tournament);
        return this;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        if (this.season != null) {
            this.season.setImage(null);
        }
        if (season != null) {
            season.setImage(this);
        }
        this.season = season;
    }

    public FileData season(Season season) {
        this.setSeason(season);
        return this;
    }

    public Camp getCamp() {
        return this.camp;
    }

    public void setCamp(Camp camp) {
        if (this.camp != null) {
            this.camp.setImage(null);
        }
        if (camp != null) {
            camp.setImage(this);
        }
        this.camp = camp;
    }

    public FileData camp(Camp camp) {
        this.setCamp(camp);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileData)) {
            return false;
        }
        return getId() != null && getId().equals(((FileData) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileData{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", fileName='" + getFileName() + "'" +
            "}";
    }
}
