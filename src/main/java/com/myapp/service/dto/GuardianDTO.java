package com.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.myapp.domain.Guardian} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuardianDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String firstName;

    @Size(max = 1)
    private String middleInitial;

    @NotNull
    @Size(max = 255)
    private String lastName;

    @NotNull
    @Size(max = 255)
    private String relationshipToPlayer;

    private Set<PlayerDTO> players = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRelationshipToPlayer() {
        return relationshipToPlayer;
    }

    public void setRelationshipToPlayer(String relationshipToPlayer) {
        this.relationshipToPlayer = relationshipToPlayer;
    }

    public Set<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<PlayerDTO> players) {
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuardianDTO)) {
            return false;
        }

        GuardianDTO guardianDTO = (GuardianDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, guardianDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuardianDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", middleInitial='" + getMiddleInitial() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", relationshipToPlayer='" + getRelationshipToPlayer() + "'" +
            ", players=" + getPlayers() +
            "}";
    }
}
