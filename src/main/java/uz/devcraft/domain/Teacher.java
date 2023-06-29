package uz.devcraft.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.devcraft.domain.enumeration.AcademicRank;

/**
 * A Teacher.
 */
@Entity
@Table(name = "teacher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "fulln_name")
    private String fullnName;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank")
    private AcademicRank rank;

    @ManyToOne(fetch = FetchType.LAZY)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spec spec;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Teacher id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullnName() {
        return this.fullnName;
    }

    public Teacher fullnName(String fullnName) {
        this.setFullnName(fullnName);
        return this;
    }

    public void setFullnName(String fullnName) {
        this.fullnName = fullnName;
    }

    public AcademicRank getRank() {
        return this.rank;
    }

    public Teacher rank(AcademicRank rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(AcademicRank rank) {
        this.rank = rank;
    }

    public Staff getStaff() {
        return this.staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Teacher staff(Staff staff) {
        this.setStaff(staff);
        return this;
    }

    public Spec getSpec() {
        return this.spec;
    }

    public void setSpec(Spec spec) {
        this.spec = spec;
    }

    public Teacher spec(Spec spec) {
        this.setSpec(spec);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teacher)) {
            return false;
        }
        return id != null && id.equals(((Teacher) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teacher{" +
            "id=" + getId() +
            ", fullnName='" + getFullnName() + "'" +
            ", rank='" + getRank() + "'" +
            "}";
    }
}
