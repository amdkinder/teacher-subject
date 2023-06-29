package uz.devcraft.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecMapperTest {

    private SpecMapper specMapper;

    @BeforeEach
    public void setUp() {
        specMapper = new SpecMapperImpl();
    }
}
