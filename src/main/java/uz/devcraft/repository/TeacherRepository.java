package uz.devcraft.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devcraft.domain.Subject;
import uz.devcraft.domain.Teacher;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for the Teacher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    List<Teacher> findAllByIdIn(Collection<Long> id);
}
