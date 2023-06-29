package uz.devcraft.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devcraft.domain.Spec;
import uz.devcraft.repository.SpecRepository;
import uz.devcraft.service.dto.SpecDTO;
import uz.devcraft.service.mapper.SpecMapper;

/**
 * Service Implementation for managing {@link Spec}.
 */
@Service
@Transactional
public class SpecService {

    private final Logger log = LoggerFactory.getLogger(SpecService.class);

    private final SpecRepository specRepository;

    private final SpecMapper specMapper;

    public SpecService(SpecRepository specRepository, SpecMapper specMapper) {
        this.specRepository = specRepository;
        this.specMapper = specMapper;
    }

    /**
     * Save a spec.
     *
     * @param specDTO the entity to save.
     * @return the persisted entity.
     */
    public SpecDTO save(SpecDTO specDTO) {
        log.debug("Request to save Spec : {}", specDTO);
        Spec spec = specMapper.toEntity(specDTO);
        spec = specRepository.save(spec);
        return specMapper.toDto(spec);
    }

    /**
     * Update a spec.
     *
     * @param specDTO the entity to save.
     * @return the persisted entity.
     */
    public SpecDTO update(SpecDTO specDTO) {
        log.debug("Request to update Spec : {}", specDTO);
        Spec spec = specMapper.toEntity(specDTO);
        spec = specRepository.save(spec);
        return specMapper.toDto(spec);
    }

    /**
     * Partially update a spec.
     *
     * @param specDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SpecDTO> partialUpdate(SpecDTO specDTO) {
        log.debug("Request to partially update Spec : {}", specDTO);

        return specRepository
            .findById(specDTO.getId())
            .map(existingSpec -> {
                specMapper.partialUpdate(existingSpec, specDTO);

                return existingSpec;
            })
            .map(specRepository::save)
            .map(specMapper::toDto);
    }

    /**
     * Get all the specs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SpecDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Specs");
        return specRepository.findAll(pageable).map(specMapper::toDto);
    }

    /**
     * Get one spec by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SpecDTO> findOne(Long id) {
        log.debug("Request to get Spec : {}", id);
        return specRepository.findById(id).map(specMapper::toDto);
    }

    /**
     * Delete the spec by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Spec : {}", id);
        specRepository.deleteById(id);
    }
}
