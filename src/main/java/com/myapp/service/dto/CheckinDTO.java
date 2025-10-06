package com.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.myapp.domain.Checkin} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckinDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant timestamp;

    @NotNull
    private PlayerDTO player;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckinDTO)) {
            return false;
        }

        CheckinDTO checkinDTO = (CheckinDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, checkinDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckinDTO{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", player=" + getPlayer() +
            "}";
    }
}
