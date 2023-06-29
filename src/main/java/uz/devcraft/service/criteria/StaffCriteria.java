package uz.devcraft.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link uz.devcraft.domain.Staff} entity. This class is used
 * in {@link uz.devcraft.web.rest.StaffResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /staff?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StaffCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter fromHours;

    private IntegerFilter toHours;

    private Boolean distinct;

    public StaffCriteria() {}

    public StaffCriteria(StaffCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.fromHours = other.fromHours == null ? null : other.fromHours.copy();
        this.toHours = other.toHours == null ? null : other.toHours.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StaffCriteria copy() {
        return new StaffCriteria(this);
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

    public IntegerFilter getFromHours() {
        return fromHours;
    }

    public IntegerFilter fromHours() {
        if (fromHours == null) {
            fromHours = new IntegerFilter();
        }
        return fromHours;
    }

    public void setFromHours(IntegerFilter fromHours) {
        this.fromHours = fromHours;
    }

    public IntegerFilter getToHours() {
        return toHours;
    }

    public IntegerFilter toHours() {
        if (toHours == null) {
            toHours = new IntegerFilter();
        }
        return toHours;
    }

    public void setToHours(IntegerFilter toHours) {
        this.toHours = toHours;
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
        final StaffCriteria that = (StaffCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(fromHours, that.fromHours) &&
            Objects.equals(toHours, that.toHours) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fromHours, toHours, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StaffCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (fromHours != null ? "fromHours=" + fromHours + ", " : "") +
            (toHours != null ? "toHours=" + toHours + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
