package io.github.ms.cloudappwatch.web.rest;

import io.github.ms.cloudappwatch.CloudappwatchApp;
import io.github.ms.cloudappwatch.domain.Server;
import io.github.ms.cloudappwatch.domain.User;
import io.github.ms.cloudappwatch.repository.ServerRepository;
import io.github.ms.cloudappwatch.service.ServerService;
import io.github.ms.cloudappwatch.web.rest.errors.ExceptionTranslator;
import io.github.ms.cloudappwatch.service.dto.ServerCriteria;
import io.github.ms.cloudappwatch.service.ServerQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static io.github.ms.cloudappwatch.web.rest.TestUtil.sameInstant;
import static io.github.ms.cloudappwatch.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.ms.cloudappwatch.domain.enumeration.ServiceStatus;
/**
 * Integration tests for the {@link ServerResource} REST controller.
 */
@SpringBootTest(classes = CloudappwatchApp.class)
public class ServerResourceIT {

    private static final String DEFAULT_HOST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_HOST_NAME = "BBBBBBBBBB";

    private static final ServiceStatus DEFAULT_STATUS = ServiceStatus.COMPLETE;
    private static final ServiceStatus UPDATED_STATUS = ServiceStatus.PARTIAL;

    private static final ZonedDateTime DEFAULT_LAST_CHECK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_CHECK = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_CHECK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerQueryService serverQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restServerMockMvc;

    private Server server;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ServerResource serverResource = new ServerResource(serverService, serverQueryService);
        this.restServerMockMvc = MockMvcBuilders.standaloneSetup(serverResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Server createEntity(EntityManager em) {
        Server server = new Server()
            .hostName(DEFAULT_HOST_NAME)
            .status(DEFAULT_STATUS)
            .lastCheck(DEFAULT_LAST_CHECK);
        return server;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Server createUpdatedEntity(EntityManager em) {
        Server server = new Server()
            .hostName(UPDATED_HOST_NAME)
            .status(UPDATED_STATUS)
            .lastCheck(UPDATED_LAST_CHECK);
        return server;
    }

    @BeforeEach
    public void initTest() {
        server = createEntity(em);
    }

    @Test
    @Transactional
    public void createServer() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server
        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isCreated());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate + 1);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getHostName()).isEqualTo(DEFAULT_HOST_NAME);
        assertThat(testServer.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testServer.getLastCheck()).isEqualTo(DEFAULT_LAST_CHECK);
    }

    @Test
    @Transactional
    public void createServerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server with an existing ID
        server.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllServers() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList
        restServerMockMvc.perform(get("/api/servers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(server.getId().intValue())))
            .andExpect(jsonPath("$.[*].hostName").value(hasItem(DEFAULT_HOST_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastCheck").value(hasItem(sameInstant(DEFAULT_LAST_CHECK))));
    }
    
    @Test
    @Transactional
    public void getServer() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", server.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(server.getId().intValue()))
            .andExpect(jsonPath("$.hostName").value(DEFAULT_HOST_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastCheck").value(sameInstant(DEFAULT_LAST_CHECK)));
    }

    @Test
    @Transactional
    public void getAllServersByHostNameIsEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where hostName equals to DEFAULT_HOST_NAME
        defaultServerShouldBeFound("hostName.equals=" + DEFAULT_HOST_NAME);

        // Get all the serverList where hostName equals to UPDATED_HOST_NAME
        defaultServerShouldNotBeFound("hostName.equals=" + UPDATED_HOST_NAME);
    }

    @Test
    @Transactional
    public void getAllServersByHostNameIsInShouldWork() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where hostName in DEFAULT_HOST_NAME or UPDATED_HOST_NAME
        defaultServerShouldBeFound("hostName.in=" + DEFAULT_HOST_NAME + "," + UPDATED_HOST_NAME);

        // Get all the serverList where hostName equals to UPDATED_HOST_NAME
        defaultServerShouldNotBeFound("hostName.in=" + UPDATED_HOST_NAME);
    }

    @Test
    @Transactional
    public void getAllServersByHostNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where hostName is not null
        defaultServerShouldBeFound("hostName.specified=true");

        // Get all the serverList where hostName is null
        defaultServerShouldNotBeFound("hostName.specified=false");
    }

    @Test
    @Transactional
    public void getAllServersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where status equals to DEFAULT_STATUS
        defaultServerShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the serverList where status equals to UPDATED_STATUS
        defaultServerShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllServersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultServerShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the serverList where status equals to UPDATED_STATUS
        defaultServerShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllServersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where status is not null
        defaultServerShouldBeFound("status.specified=true");

        // Get all the serverList where status is null
        defaultServerShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck equals to DEFAULT_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.equals=" + DEFAULT_LAST_CHECK);

        // Get all the serverList where lastCheck equals to UPDATED_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.equals=" + UPDATED_LAST_CHECK);
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsInShouldWork() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck in DEFAULT_LAST_CHECK or UPDATED_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.in=" + DEFAULT_LAST_CHECK + "," + UPDATED_LAST_CHECK);

        // Get all the serverList where lastCheck equals to UPDATED_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.in=" + UPDATED_LAST_CHECK);
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsNullOrNotNull() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck is not null
        defaultServerShouldBeFound("lastCheck.specified=true");

        // Get all the serverList where lastCheck is null
        defaultServerShouldNotBeFound("lastCheck.specified=false");
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck is greater than or equal to DEFAULT_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.greaterThanOrEqual=" + DEFAULT_LAST_CHECK);

        // Get all the serverList where lastCheck is greater than or equal to UPDATED_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.greaterThanOrEqual=" + UPDATED_LAST_CHECK);
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck is less than or equal to DEFAULT_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.lessThanOrEqual=" + DEFAULT_LAST_CHECK);

        // Get all the serverList where lastCheck is less than or equal to SMALLER_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.lessThanOrEqual=" + SMALLER_LAST_CHECK);
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsLessThanSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck is less than DEFAULT_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.lessThan=" + DEFAULT_LAST_CHECK);

        // Get all the serverList where lastCheck is less than UPDATED_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.lessThan=" + UPDATED_LAST_CHECK);
    }

    @Test
    @Transactional
    public void getAllServersByLastCheckIsGreaterThanSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList where lastCheck is greater than DEFAULT_LAST_CHECK
        defaultServerShouldNotBeFound("lastCheck.greaterThan=" + DEFAULT_LAST_CHECK);

        // Get all the serverList where lastCheck is greater than SMALLER_LAST_CHECK
        defaultServerShouldBeFound("lastCheck.greaterThan=" + SMALLER_LAST_CHECK);
    }


    @Test
    @Transactional
    public void getAllServersByAdminIsEqualToSomething() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);
        User admin = UserResourceIT.createEntity(em);
        em.persist(admin);
        em.flush();
        server.setAdmin(admin);
        serverRepository.saveAndFlush(server);
        Long adminId = admin.getId();

        // Get all the serverList where admin equals to adminId
        defaultServerShouldBeFound("adminId.equals=" + adminId);

        // Get all the serverList where admin equals to adminId + 1
        defaultServerShouldNotBeFound("adminId.equals=" + (adminId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServerShouldBeFound(String filter) throws Exception {
        restServerMockMvc.perform(get("/api/servers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(server.getId().intValue())))
            .andExpect(jsonPath("$.[*].hostName").value(hasItem(DEFAULT_HOST_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastCheck").value(hasItem(sameInstant(DEFAULT_LAST_CHECK))));

        // Check, that the count call also returns 1
        restServerMockMvc.perform(get("/api/servers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServerShouldNotBeFound(String filter) throws Exception {
        restServerMockMvc.perform(get("/api/servers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServerMockMvc.perform(get("/api/servers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingServer() throws Exception {
        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServer() throws Exception {
        // Initialize the database
        serverService.save(server);

        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Update the server
        Server updatedServer = serverRepository.findById(server.getId()).get();
        // Disconnect from session so that the updates on updatedServer are not directly saved in db
        em.detach(updatedServer);
        updatedServer
            .hostName(UPDATED_HOST_NAME)
            .status(UPDATED_STATUS)
            .lastCheck(UPDATED_LAST_CHECK);

        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServer)))
            .andExpect(status().isOk());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getHostName()).isEqualTo(UPDATED_HOST_NAME);
        assertThat(testServer.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testServer.getLastCheck()).isEqualTo(UPDATED_LAST_CHECK);
    }

    @Test
    @Transactional
    public void updateNonExistingServer() throws Exception {
        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Create the Server

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteServer() throws Exception {
        // Initialize the database
        serverService.save(server);

        int databaseSizeBeforeDelete = serverRepository.findAll().size();

        // Delete the server
        restServerMockMvc.perform(delete("/api/servers/{id}", server.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Server.class);
        Server server1 = new Server();
        server1.setId(1L);
        Server server2 = new Server();
        server2.setId(server1.getId());
        assertThat(server1).isEqualTo(server2);
        server2.setId(2L);
        assertThat(server1).isNotEqualTo(server2);
        server1.setId(null);
        assertThat(server1).isNotEqualTo(server2);
    }
}
