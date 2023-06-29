package uz.devcraft.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devcraft.web.rest.TestUtil;

class TeacherSubjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherSubject.class);
        TeacherSubject teacherSubject1 = new TeacherSubject();
        teacherSubject1.setId(1L);
        TeacherSubject teacherSubject2 = new TeacherSubject();
        teacherSubject2.setId(teacherSubject1.getId());
        assertThat(teacherSubject1).isEqualTo(teacherSubject2);
        teacherSubject2.setId(2L);
        assertThat(teacherSubject1).isNotEqualTo(teacherSubject2);
        teacherSubject1.setId(null);
        assertThat(teacherSubject1).isNotEqualTo(teacherSubject2);
    }
}
