package uz.devcraft.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link uz.devcraft.domain.Subject} entity. This class is used
 * in {@link uz.devcraft.web.rest.SubjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /subjects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubjectCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter hours;

    private BooleanFilter inUse;

    private LongFilter specId;

    private Boolean distinct;

    public SubjectCriteria() {}

    public SubjectCriteria(SubjectCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.hours = other.hours == null ? null : other.hours.copy();
        this.inUse = other.inUse == null ? null : other.inUse.copy();
        this.specId = other.specId == null ? null : other.specId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SubjectCriteria copy() {
        return new SubjectCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getHours() {
        return hours;
    }

    public IntegerFilter hours() {
        if (hours == null) {
            hours = new IntegerFilter();
        }
        return hours;
    }

    public void setHours(IntegerFilter hours) {
        this.hours = hours;
    }

    public BooleanFilter getInUse() {
        return inUse;
    }

    public BooleanFilter inUse() {
        if (inUse == null) {
            inUse = new BooleanFilter();
        }
        return inUse;
    }

    public void setInUse(BooleanFilter inUse) {
        this.inUse = inUse;
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
        final SubjectCriteria that = (SubjectCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(hours, that.hours) &&
            Objects.equals(inUse, that.inUse) &&
            Objects.equals(specId, that.specId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, hours, inUse, specId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubjectCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (hours != null ? "hours=" + hours + ", " : "") +
            (inUse != null ? "inUse=" + inUse + ", " : "") +
            (specId != null ? "specId=" + specId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
