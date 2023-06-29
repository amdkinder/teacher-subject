package uz.devcraft.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devcraft.web.rest.TestUtil;

class SpecDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpecDTO.class);
        SpecDTO specDTO1 = new SpecDTO();
        specDTO1.setId(1L);
        SpecDTO specDTO2 = new SpecDTO();
        assertThat(specDTO1).isNotEqualTo(specDTO2);
        specDTO2.setId(specDTO1.getId());
        assertThat(specDTO1).isEqualTo(specDTO2);
        specDTO2.setId(2L);
        assertThat(specDTO1).isNotEqualTo(specDTO2);
        specDTO1.setId(null);
        assertThat(specDTO1).isNotEqualTo(specDTO2);
    }
}
