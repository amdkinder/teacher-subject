package uz.devcraft.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devcraft.web.rest.TestUtil;

class SpecTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spec.class);
        Spec spec1 = new Spec();
        spec1.setId(1L);
        Spec spec2 = new Spec();
        spec2.setId(spec1.getId());
        assertThat(spec1).isEqualTo(spec2);
        spec2.setId(2L);
        assertThat(spec1).isNotEqualTo(spec2);
        spec1.setId(null);
        assertThat(spec1).isNotEqualTo(spec2);
    }
}
