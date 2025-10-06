package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Guardian.
 */
@Entity
@Table(name = "guardian")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Guardian implements Serializable {

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

    @NotNull
    @Size(max = 255)
    @Column(name = "relationship_to_player", length = 255, nullable = false)
    private String relationshipToPlayer;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "test_field")
    private String testField;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "guardians")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "checkins", "guardians", "teams" }, allowSetters = true)
    private Set<Player> players = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Guardian id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Guardian firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return this.middleInitial;
    }

    public Guardian middleInitial(String middleInitial) {
        this.setMiddleInitial(middleInitial);
        return this;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Guardian lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRelationshipToPlayer() {
        return this.relationshipToPlayer;
    }

    public Guardian relationshipToPlayer(String relationshipToPlayer) {
        this.setRelationshipToPlayer(relationshipToPlayer);
        return this;
    }

    public void setRelationshipToPlayer(String relationshipToPlayer) {
        this.relationshipToPlayer = relationshipToPlayer;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Guardian dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTestField() {
        return this.testField;
    }

    public Guardian testField(String testField) {
        this.setTestField(testField);
        return this;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<Player> players) {
        if (this.players != null) {
            this.players.forEach(i -> i.removeGuardians(this));
        }
        if (players != null) {
            players.forEach(i -> i.addGuardians(this));
        }
        this.players = players;
    }

    public Guardian players(Set<Player> players) {
        this.setPlayers(players);
        return this;
    }

    public Guardian addPlayers(Player player) {
        this.players.add(player);
        player.getGuardians().add(this);
        return this;
    }

    public Guardian removePlayers(Player player) {
        this.players.remove(player);
        player.getGuardians().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Guardian)) {
            return false;
        }
        return getId() != null && getId().equals(((Guardian) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Guardian{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", middleInitial='" + getMiddleInitial() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", relationshipToPlayer='" + getRelationshipToPlayer() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", testField='" + getTestField() + "'" +
            "}";
    }
}
