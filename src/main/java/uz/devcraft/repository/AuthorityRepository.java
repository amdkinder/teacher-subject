package uz.devcraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.devcraft.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
