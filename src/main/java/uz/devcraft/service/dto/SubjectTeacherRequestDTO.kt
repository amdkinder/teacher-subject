package uz.devcraft.service.dto

data class SubjectTeacherRequestDTO (
    var teacherId: Long? = null,
    var subjectIds: List<Long>? = emptyList(),
)
