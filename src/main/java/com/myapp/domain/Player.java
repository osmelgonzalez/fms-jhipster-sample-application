package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myapp.domain.enumeration.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Player.
 */
@Entity
@Table(name = "player")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Size(max = 1)
    @Column(name = "middle_initial", length = 1)
    private String middleInitial;

    @NotNull
    @Size(max = 255)
    @Column(name = "last_name", length = 255, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "player")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "player" }, allowSetters = true)
    private Set<Checkin> checkins = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_player__guardians",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "guardians_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "players" }, allowSetters = true)
    private Set<Guardian> guardians = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "players")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "players" }, allowSetters = true)
    private Set<Team> teams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Player id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Player firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return this.middleInitial;
    }

    public Player middleInitial(String middleInitial) {
        this.setMiddleInitial(middleInitial);
        return this;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Player lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Player gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Player dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<Checkin> getCheckins() {
        return this.checkins;
    }

    public void setCheckins(Set<Checkin> checkins) {
        if (this.checkins != null) {
            this.checkins.forEach(i -> i.setPlayer(null));
        }
        if (checkins != null) {
            checkins.forEach(i -> i.setPlayer(this));
        }
        this.checkins = checkins;
    }

    public Player checkins(Set<Checkin> checkins) {
        this.setCheckins(checkins);
        return this;
    }

    public Player addCheckins(Checkin checkin) {
        this.checkins.add(checkin);
        checkin.setPlayer(this);
        return this;
    }

    public Player removeCheckins(Checkin checkin) {
        this.checkins.remove(checkin);
        checkin.setPlayer(null);
        return this;
    }

    public Set<Guardian> getGuardians() {
        return this.guardians;
    }

    public void setGuardians(Set<Guardian> guardians) {
        this.guardians = guardians;
    }

    public Player guardians(Set<Guardian> guardians) {
        this.setGuardians(guardians);
        return this;
    }

    public Player addGuardians(Guardian guardian) {
        this.guardians.add(guardian);
        return this;
    }

    public Player removeGuardians(Guardian guardian) {
        this.guardians.remove(guardian);
        return this;
    }

    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<Team> teams) {
        if (this.teams != null) {
            this.teams.forEach(i -> i.removePlayers(this));
        }
        if (teams != null) {
            teams.forEach(i -> i.addPlayers(this));
        }
        this.teams = teams;
    }

    public Player teams(Set<Team> teams) {
        this.setTeams(teams);
        return this;
    }

    public Player addTeams(Team team) {
        this.teams.add(team);
        team.getPlayers().add(this);
        return this;
    }

    public Player removeTeams(Team team) {
        this.teams.remove(team);
        team.getPlayers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        return getId() != null && getId().equals(((Player) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", middleInitial='" + getMiddleInitial() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", gender='" + getGender() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            "}";
    }
}
