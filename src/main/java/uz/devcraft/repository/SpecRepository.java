package uz.devcraft.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devcraft.domain.Spec;

/**
 * Spring Data JPA repository for the Spec entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpecRepository extends JpaRepository<Spec, Long>, JpaSpecificationExecutor<Spec> {}
