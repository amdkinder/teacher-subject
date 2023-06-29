package uz.devcraft.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devcraft.domain.TeacherSubject;

/**
 * Spring Data JPA repository for the TeacherSubject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long>, JpaSpecificationExecutor<TeacherSubject> {
    @Query(value = "select sum(s.hours) from Subject s where s.id in (select ts.subject.id from TeacherSubject ts where ts.teacher.id = :teacherId)")
    Integer sumTeacherSubjectHours(Long teacherId);
}
