package uz.devcraft.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devcraft.domain.Subject;
import uz.devcraft.domain.TeacherSubject;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the TeacherSubject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long>, JpaSpecificationExecutor<TeacherSubject> {
    @Query(value = "select sum(s.hours) from Subject s where s.id in (select ts.subject.id from TeacherSubject ts where ts.teacher.id = :teacherId)")
    Integer sumTeacherSubjectHours(@Param("teacherId") Long teacherId);

    @Query("select ts.subject from TeacherSubject ts where ts.teacher.id = :teacherId")
    List<Subject> findAllSubjectsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("select ts from TeacherSubject ts where ts.teacher.id = :teacherId and ts.subject.id = :subjectId")
    Optional<TeacherSubject> findFirstByTeacherAndSubject(@Param("teacherId") Long teacherId, @Param("subjectId") Long subjectId);
}
