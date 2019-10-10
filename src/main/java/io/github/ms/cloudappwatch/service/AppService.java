package io.github.ms.cloudappwatch.service;

import io.github.ms.cloudappwatch.domain.App;
import io.github.ms.cloudappwatch.repository.AppRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link App}.
 */
@Service
@Transactional
public class AppService {

    private final Logger log = LoggerFactory.getLogger(AppService.class);

    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    /**
     * Save a app.
     *
     * @param app the entity to save.
     * @return the persisted entity.
     */
    public App save(App app) {
        log.debug("Request to save App : {}", app);
        return appRepository.save(app);
    }

    /**
     * Get all the apps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<App> findAll(Pageable pageable) {
        log.debug("Request to get all Apps");
        return appRepository.findAll(pageable);
    }


    /**
     * Get one app by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<App> findOne(Long id) {
        log.debug("Request to get App : {}", id);
        return appRepository.findById(id);
    }

    /**
     * Delete the app by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete App : {}", id);
        appRepository.deleteById(id);
    }
}
