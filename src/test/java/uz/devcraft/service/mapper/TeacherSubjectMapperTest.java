package uz.devcraft.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeacherSubjectMapperTest {

    private TeacherSubjectMapper teacherSubjectMapper;

    @BeforeEach
    public void setUp() {
        teacherSubjectMapper = new TeacherSubjectMapperImpl();
    }
}
