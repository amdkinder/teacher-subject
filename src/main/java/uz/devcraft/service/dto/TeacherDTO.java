package uz.devcraft.service.dto;

import java.io.Serializable;
import java.util.Objects;
import uz.devcraft.domain.enumeration.AcademicRank;

/**
 * A DTO for the {@link uz.devcraft.domain.Teacher} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherDTO implements Serializable {

    private Long id;

    private String fullnName;

    private AcademicRank rank;

    private StaffDTO staff;

    private SpecDTO spec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullnName() {
        return fullnName;
    }

    public void setFullnName(String fullnName) {
        this.fullnName = fullnName;
    }

    public AcademicRank getRank() {
        return rank;
    }

    public void setRank(AcademicRank rank) {
        this.rank = rank;
    }

    public StaffDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
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
        if (!(o instanceof TeacherDTO)) {
            return false;
        }

        TeacherDTO teacherDTO = (TeacherDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teacherDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherDTO{" +
            "id=" + getId() +
            ", fullnName='" + getFullnName() + "'" +
            ", rank='" + getRank() + "'" +
            ", staff=" + getStaff() +
            ", spec=" + getSpec() +
            "}";
    }
}
