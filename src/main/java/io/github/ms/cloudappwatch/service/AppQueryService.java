package io.github.ms.cloudappwatch.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.ms.cloudappwatch.domain.App;
import io.github.ms.cloudappwatch.domain.*; // for static metamodels
import io.github.ms.cloudappwatch.repository.AppRepository;
import io.github.ms.cloudappwatch.service.dto.AppCriteria;

/**
 * Service for executing complex queries for {@link App} entities in the database.
 * The main input is a {@link AppCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link App} or a {@link Page} of {@link App} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppQueryService extends QueryService<App> {

    private final Logger log = LoggerFactory.getLogger(AppQueryService.class);

    private final AppRepository appRepository;

    public AppQueryService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    /**
     * Return a {@link List} of {@link App} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<App> findByCriteria(AppCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<App> specification = createSpecification(criteria);
        return appRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link App} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<App> findByCriteria(AppCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<App> specification = createSpecification(criteria);
        return appRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<App> specification = createSpecification(criteria);
        return appRepository.count(specification);
    }

    /**
     * Function to convert {@link AppCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<App> createSpecification(AppCriteria criteria) {
        Specification<App> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), App_.id));
            }
            if (criteria.getServiceFlag() != null) {
                specification = specification.and(buildSpecification(criteria.getServiceFlag(), App_.serviceFlag));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), App_.status));
            }
            if (criteria.getServerId() != null) {
                specification = specification.and(buildSpecification(criteria.getServerId(),
                    root -> root.join(App_.server, JoinType.LEFT).get(Server_.id)));
            }
        }
        return specification;
    }
}
