package uz.devcraft.service

import org.springframework.stereotype.Service
import uz.devcraft.domain.Subject
import uz.devcraft.domain.Teacher
import uz.devcraft.domain.TeacherSubject
import uz.devcraft.exception.InternalException
import uz.devcraft.exception.NotFoundException
import uz.devcraft.repository.SubjectRepository
import uz.devcraft.repository.TeacherRepository
import uz.devcraft.repository.TeacherSubjectRepository
import uz.devcraft.service.dto.SubjectDTO
import uz.devcraft.service.dto.TeacherSubjectDTO
import uz.devcraft.service.mapper.SubjectMapper
import uz.devcraft.service.mapper.TeacherMapper
import uz.devcraft.service.mapper.TeacherSubjectMapper

@Service
open class TeacherSubjectConnectService(
    private val subjectRepository: SubjectRepository,
    private val subjectMapper: SubjectMapper,
    private val teacherSubjectRepository: TeacherSubjectRepository,
    private val teacherSubjectMapper: TeacherSubjectMapper,
    private val teacherRepository: TeacherRepository,
    private val teacherMapper: TeacherMapper,
) {

    fun calculateTeacherHours(teacherId: Long): Int {
        return teacherSubjectRepository.sumTeacherSubjectHours(teacherId)
    }

    fun getSubjectsByTeacherId(teacherId: Long): List<SubjectDTO> {
        val result = teacherSubjectRepository.findAllSubjectsByTeacherId(teacherId)
        return result.map { subjectMapper.toDto(it) }
    }

    private fun addSubjectToTeacher(teacher: Teacher, subject: Subject): TeacherSubjectDTO {

        checkSubjectAndTeacher(teacher, subject)
        val teacherSubject = TeacherSubject().apply {
            this.teacher = teacher
            this.subject = subject
        }
        val result = teacherSubjectRepository.save(teacherSubject)
        subjectRepository.save(subject.apply { inUse = true })
        return teacherSubjectMapper.toDto(result)
    }

    fun addSubjects(teacherId: Long, subjectIds: List<Long>): List<TeacherSubjectDTO> {
        val teacher = teacherRepository.findById(teacherId)
            .orElseThrow { NotFoundException("Teacher not found by id: $teacherId") }
        val subjects = subjectRepository.findAllByIdIn(subjectIds)
        return subjects.map { addSubjectToTeacher(teacher, it) }
    }

    private fun checkSubjectAndTeacher(teacher: Teacher, subject: Subject) {
        if (teacher.spec?.id == null)
            throw InternalException("teacher spec id must be not null")

        if (subject.spec?.id == null)
            throw InternalException("subject spec id must be not null")

        if (subject.spec?.id == teacher.spec?.id)
            throw InternalException("teacher spec not equal to subject spec")

        if (subject.inUse) {
            throw InternalException("Subject used to other teacher")
        }
    }
}
