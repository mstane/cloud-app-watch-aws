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

import io.github.ms.cloudappwatch.domain.Server;
import io.github.ms.cloudappwatch.domain.*; // for static metamodels
import io.github.ms.cloudappwatch.repository.ServerRepository;
import io.github.ms.cloudappwatch.service.dto.ServerCriteria;

/**
 * Service for executing complex queries for {@link Server} entities in the database.
 * The main input is a {@link ServerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Server} or a {@link Page} of {@link Server} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServerQueryService extends QueryService<Server> {

    private final Logger log = LoggerFactory.getLogger(ServerQueryService.class);

    private final ServerRepository serverRepository;

    public ServerQueryService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    /**
     * Return a {@link List} of {@link Server} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Server> findByCriteria(ServerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Server> specification = createSpecification(criteria);
        return serverRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Server} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Server> findByCriteria(ServerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Server> specification = createSpecification(criteria);
        return serverRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Server> specification = createSpecification(criteria);
        return serverRepository.count(specification);
    }

    /**
     * Function to convert {@link ServerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Server> createSpecification(ServerCriteria criteria) {
        Specification<Server> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Server_.id));
            }
            if (criteria.getHostName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHostName(), Server_.hostName));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Server_.status));
            }
            if (criteria.getLastCheck() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastCheck(), Server_.lastCheck));
            }
            if (criteria.getAdminId() != null) {
                specification = specification.and(buildSpecification(criteria.getAdminId(),
                    root -> root.join(Server_.admin, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
