package uz.devcraft.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devcraft.domain.Spec} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpecDTO implements Serializable {

    private Long id;

    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpecDTO)) {
            return false;
        }

        SpecDTO specDTO = (SpecDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, specDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpecDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
