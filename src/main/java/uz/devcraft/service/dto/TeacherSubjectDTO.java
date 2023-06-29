package uz.devcraft.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devcraft.domain.TeacherSubject} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherSubjectDTO implements Serializable {

    private Long id;

    private TeacherDTO teacher;

    private SubjectDTO subject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeacherSubjectDTO)) {
            return false;
        }

        TeacherSubjectDTO teacherSubjectDTO = (TeacherSubjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teacherSubjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherSubjectDTO{" +
            "id=" + getId() +
            ", teacher=" + getTeacher() +
            ", subject=" + getSubject() +
            "}";
    }
}
