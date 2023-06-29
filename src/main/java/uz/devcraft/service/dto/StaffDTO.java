package uz.devcraft.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devcraft.domain.Staff} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StaffDTO implements Serializable {

    private Long id;

    private String name;

    private Integer fromHours;

    private Integer toHours;

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

    public Integer getFromHours() {
        return fromHours;
    }

    public void setFromHours(Integer fromHours) {
        this.fromHours = fromHours;
    }

    public Integer getToHours() {
        return toHours;
    }

    public void setToHours(Integer toHours) {
        this.toHours = toHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaffDTO)) {
            return false;
        }

        StaffDTO staffDTO = (StaffDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, staffDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StaffDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fromHours=" + getFromHours() +
            ", toHours=" + getToHours() +
            "}";
    }
}
