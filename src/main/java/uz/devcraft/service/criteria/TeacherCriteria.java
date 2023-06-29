package uz.devcraft.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import uz.devcraft.domain.enumeration.AcademicRank;

/**
 * Criteria class for the {@link uz.devcraft.domain.Teacher} entity. This class is used
 * in {@link uz.devcraft.web.rest.TeacherResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teachers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AcademicRank
     */
    public static class AcademicRankFilter extends Filter<AcademicRank> {

        public AcademicRankFilter() {}

        public AcademicRankFilter(AcademicRankFilter filter) {
            super(filter);
        }

        @Override
        public AcademicRankFilter copy() {
            return new AcademicRankFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullnName;

    private AcademicRankFilter rank;

    private LongFilter staffId;

    private LongFilter specId;

    private Boolean distinct;

    public TeacherCriteria() {}

    public TeacherCriteria(TeacherCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullnName = other.fullnName == null ? null : other.fullnName.copy();
        this.rank = other.rank == null ? null : other.rank.copy();
        this.staffId = other.staffId == null ? null : other.staffId.copy();
        this.specId = other.specId == null ? null : other.specId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TeacherCriteria copy() {
        return new TeacherCriteria(this);
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

    public StringFilter getFullnName() {
        return fullnName;
    }

    public StringFilter fullnName() {
        if (fullnName == null) {
            fullnName = new StringFilter();
        }
        return fullnName;
    }

    public void setFullnName(StringFilter fullnName) {
        this.fullnName = fullnName;
    }

    public AcademicRankFilter getRank() {
        return rank;
    }

    public AcademicRankFilter rank() {
        if (rank == null) {
            rank = new AcademicRankFilter();
        }
        return rank;
    }

    public void setRank(AcademicRankFilter rank) {
        this.rank = rank;
    }

    public LongFilter getStaffId() {
        return staffId;
    }

    public LongFilter staffId() {
        if (staffId == null) {
            staffId = new LongFilter();
        }
        return staffId;
    }

    public void setStaffId(LongFilter staffId) {
        this.staffId = staffId;
    }

    public LongFilter getSpecId() {
        return specId;
    }

    public LongFilter specId() {
        if (specId == null) {
            specId = new LongFilter();
        }
        return specId;
    }

    public void setSpecId(LongFilter specId) {
        this.specId = specId;
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
        final TeacherCriteria that = (TeacherCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullnName, that.fullnName) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(staffId, that.staffId) &&
            Objects.equals(specId, that.specId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullnName, rank, staffId, specId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fullnName != null ? "fullnName=" + fullnName + ", " : "") +
            (rank != null ? "rank=" + rank + ", " : "") +
            (staffId != null ? "staffId=" + staffId + ", " : "") +
            (specId != null ? "specId=" + specId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
