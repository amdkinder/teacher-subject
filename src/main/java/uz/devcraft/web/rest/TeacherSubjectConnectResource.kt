package uz.devcraft.web.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import uz.devcraft.domain.RestResult
import uz.devcraft.service.TeacherSubjectConnectService
import uz.devcraft.service.dto.SubjectTeacherRequestDTO
import uz.devcraft.service.dto.TeacherSubjectDTO

@RequestMapping("/api/teacher-subject-connect")
open class TeacherSubjectConnectResource(
    private val teacherSubjectConnectService: TeacherSubjectConnectService
) {

    @GetMapping("/by-teacher/{id}")
    fun getSumHoursByTeacher(@PathVariable("id") teacherId: Long): ResponseEntity<*> {
        val result = teacherSubjectConnectService.calculateTeacherHours(teacherId)
        return ResponseEntity.ok(RestResult.success(result))
    }

    @PostMapping
    fun connectSubjectsToTeacher(@RequestBody request: SubjectTeacherRequestDTO): ResponseEntity<RestResult<List<TeacherSubjectDTO>>> {
        val result = teacherSubjectConnectService.addSubjects(request.teacherId!!, request.subjectIds!!)
        return ResponseEntity.ok(RestResult.success(result))
    }


}
