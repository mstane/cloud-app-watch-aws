package io.github.ms.cloudappwatch.service;

import io.github.ms.cloudappwatch.domain.Server;
import io.github.ms.cloudappwatch.repository.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Server}.
 */
@Service
@Transactional
public class ServerService {

    private final Logger log = LoggerFactory.getLogger(ServerService.class);

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    /**
     * Save a server.
     *
     * @param server the entity to save.
     * @return the persisted entity.
     */
    public Server save(Server server) {
        log.debug("Request to save Server : {}", server);
        return serverRepository.save(server);
    }

    /**
     * Get all the servers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Server> findAll(Pageable pageable) {
        log.debug("Request to get all Servers");
        return serverRepository.findAll(pageable);
    }


    /**
     * Get one server by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Server> findOne(Long id) {
        log.debug("Request to get Server : {}", id);
        return serverRepository.findById(id);
    }

    /**
     * Delete the server by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Server : {}", id);
        serverRepository.deleteById(id);
    }
}
