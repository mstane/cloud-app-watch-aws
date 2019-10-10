package io.github.ms.cloudappwatch.web.rest;

import io.github.ms.cloudappwatch.domain.App;
import io.github.ms.cloudappwatch.service.AppService;
import io.github.ms.cloudappwatch.web.rest.errors.BadRequestAlertException;
import io.github.ms.cloudappwatch.service.dto.AppCriteria;
import io.github.ms.cloudappwatch.service.AppQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link io.github.ms.cloudappwatch.domain.App}.
 */
@RestController
@RequestMapping("/api")
public class AppResource {

    private final Logger log = LoggerFactory.getLogger(AppResource.class);

    private static final String ENTITY_NAME = "app";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppService appService;

    private final AppQueryService appQueryService;

    public AppResource(AppService appService, AppQueryService appQueryService) {
        this.appService = appService;
        this.appQueryService = appQueryService;
    }

    /**
     * {@code POST  /apps} : Create a new app.
     *
     * @param app the app to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new app, or with status {@code 400 (Bad Request)} if the app has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/apps")
    public ResponseEntity<App> createApp(@RequestBody App app) throws URISyntaxException {
        log.debug("REST request to save App : {}", app);
        if (app.getId() != null) {
            throw new BadRequestAlertException("A new app cannot already have an ID", ENTITY_NAME, "idexists");
        }
        App result = appService.save(app);
        return ResponseEntity.created(new URI("/api/apps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /apps} : Updates an existing app.
     *
     * @param app the app to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated app,
     * or with status {@code 400 (Bad Request)} if the app is not valid,
     * or with status {@code 500 (Internal Server Error)} if the app couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/apps")
    public ResponseEntity<App> updateApp(@RequestBody App app) throws URISyntaxException {
        log.debug("REST request to update App : {}", app);
        if (app.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        App result = appService.save(app);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, app.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /apps} : get all the apps.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of apps in body.
     */
    @GetMapping("/apps")
    public ResponseEntity<List<App>> getAllApps(AppCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Apps by criteria: {}", criteria);
        Page<App> page = appQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /apps/count} : count all the apps.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/apps/count")
    public ResponseEntity<Long> countApps(AppCriteria criteria) {
        log.debug("REST request to count Apps by criteria: {}", criteria);
        return ResponseEntity.ok().body(appQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /apps/:id} : get the "id" app.
     *
     * @param id the id of the app to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the app, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/apps/{id}")
    public ResponseEntity<App> getApp(@PathVariable Long id) {
        log.debug("REST request to get App : {}", id);
        Optional<App> app = appService.findOne(id);
        return ResponseUtil.wrapOrNotFound(app);
    }

    /**
     * {@code DELETE  /apps/:id} : delete the "id" app.
     *
     * @param id the id of the app to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/apps/{id}")
    public ResponseEntity<Void> deleteApp(@PathVariable Long id) {
        log.debug("REST request to delete App : {}", id);
        appService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
