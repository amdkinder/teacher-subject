package uz.devcraft.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link uz.devcraft.domain.TeacherSubject} entity. This class is used
 * in {@link uz.devcraft.web.rest.TeacherSubjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teacher-subjects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherSubjectCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter teacherId;

    private LongFilter subjectId;

    private Boolean distinct;

    public TeacherSubjectCriteria() {}

    public TeacherSubjectCriteria(TeacherSubjectCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.teacherId = other.teacherId == null ? null : other.teacherId.copy();
        this.subjectId = other.subjectId == null ? null : other.subjectId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TeacherSubjectCriteria copy() {
        return new TeacherSubjectCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getTeacherId() {
        return teacherId;
    }

    public LongFilter teacherId() {
        if (teacherId == null) {
            teacherId = new LongFilter();
        }
        return teacherId;
    }

    public void setTeacherId(LongFilter teacherId) {
        this.teacherId = teacherId;
    }

    public LongFilter getSubjectId() {
        return subjectId;
    }

    public LongFilter subjectId() {
        if (subjectId == null) {
            subjectId = new LongFilter();
        }
        return subjectId;
    }

    public void setSubjectId(LongFilter subjectId) {
        this.subjectId = subjectId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TeacherSubjectCriteria that = (TeacherSubjectCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(teacherId, that.teacherId) &&
            Objects.equals(subjectId, that.subjectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teacherId, subjectId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherSubjectCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (teacherId != null ? "teacherId=" + teacherId + ", " : "") +
            (subjectId != null ? "subjectId=" + subjectId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
