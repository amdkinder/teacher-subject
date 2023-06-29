package uz.devcraft.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devcraft.web.rest.TestUtil;

class TeacherSubjectDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherSubjectDTO.class);
        TeacherSubjectDTO teacherSubjectDTO1 = new TeacherSubjectDTO();
        teacherSubjectDTO1.setId(1L);
        TeacherSubjectDTO teacherSubjectDTO2 = new TeacherSubjectDTO();
        assertThat(teacherSubjectDTO1).isNotEqualTo(teacherSubjectDTO2);
        teacherSubjectDTO2.setId(teacherSubjectDTO1.getId());
        assertThat(teacherSubjectDTO1).isEqualTo(teacherSubjectDTO2);
        teacherSubjectDTO2.setId(2L);
        assertThat(teacherSubjectDTO1).isNotEqualTo(teacherSubjectDTO2);
        teacherSubjectDTO1.setId(null);
        assertThat(teacherSubjectDTO1).isNotEqualTo(teacherSubjectDTO2);
    }
}
