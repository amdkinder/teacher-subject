package uz.devcraft.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devcraft.domain.Subject} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubjectDTO implements Serializable {

    private Long id;

    private String name;

    private Integer hours;

    private Boolean inUse;

    private SpecDTO spec;

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

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public SpecDTO getSpec() {
        return spec;
    }

    public void setSpec(SpecDTO spec) {
        this.spec = spec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubjectDTO)) {
            return false;
        }

        SubjectDTO subjectDTO = (SubjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubjectDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", hours=" + getHours() +
            ", inUse='" + getInUse() + "'" +
            ", spec=" + getSpec() +
            "}";
    }
}
