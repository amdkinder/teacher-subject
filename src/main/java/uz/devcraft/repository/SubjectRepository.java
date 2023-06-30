package uz.devcraft.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devcraft.domain.Subject;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for the Subject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {

    @Query("select s from Subject s where s.inUse = false and s.spec.id = :specId")
    List<Subject> findAllBySpecAndNotUse(@Param("specId") Long specId);

    List<Subject> findAllByIdIn(Collection<Long> id);
}
