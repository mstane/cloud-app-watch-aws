package io.github.ms.cloudappwatch.web.rest;

import io.github.ms.cloudappwatch.CloudappwatchApp;
import io.github.ms.cloudappwatch.domain.App;
import io.github.ms.cloudappwatch.domain.Server;
import io.github.ms.cloudappwatch.repository.AppRepository;
import io.github.ms.cloudappwatch.service.AppService;
import io.github.ms.cloudappwatch.web.rest.errors.ExceptionTranslator;
import io.github.ms.cloudappwatch.service.dto.AppCriteria;
import io.github.ms.cloudappwatch.service.AppQueryService;

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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.github.ms.cloudappwatch.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.ms.cloudappwatch.domain.enumeration.AppStatus;
/**
 * Integration tests for the {@link AppResource} REST controller.
 */
@SpringBootTest(classes = CloudappwatchApp.class)
public class AppResourceIT {

    private static final String DEFAULT_COMMAND_LINE = "AAAAAAAAAA";
    private static final String UPDATED_COMMAND_LINE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SERVICE_FLAG = false;
    private static final Boolean UPDATED_SERVICE_FLAG = true;

    private static final AppStatus DEFAULT_STATUS = AppStatus.UP;
    private static final AppStatus UPDATED_STATUS = AppStatus.DOWN;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AppService appService;

    @Autowired
    private AppQueryService appQueryService;

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

    private MockMvc restAppMockMvc;

    private App app;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AppResource appResource = new AppResource(appService, appQueryService);
        this.restAppMockMvc = MockMvcBuilders.standaloneSetup(appResource)
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
    public static App createEntity(EntityManager em) {
        App app = new App()
            .commandLine(DEFAULT_COMMAND_LINE)
            .serviceFlag(DEFAULT_SERVICE_FLAG)
            .status(DEFAULT_STATUS);
        return app;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static App createUpdatedEntity(EntityManager em) {
        App app = new App()
            .commandLine(UPDATED_COMMAND_LINE)
            .serviceFlag(UPDATED_SERVICE_FLAG)
            .status(UPDATED_STATUS);
        return app;
    }

    @BeforeEach
    public void initTest() {
        app = createEntity(em);
    }

    @Test
    @Transactional
    public void createApp() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App
        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isCreated());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeCreate + 1);
        App testApp = appList.get(appList.size() - 1);
        assertThat(testApp.getCommandLine()).isEqualTo(DEFAULT_COMMAND_LINE);
        assertThat(testApp.isServiceFlag()).isEqualTo(DEFAULT_SERVICE_FLAG);
        assertThat(testApp.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createAppWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App with an existing ID
        app.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllApps() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList
        restAppMockMvc.perform(get("/api/apps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId().intValue())))
            .andExpect(jsonPath("$.[*].commandLine").value(hasItem(DEFAULT_COMMAND_LINE.toString())))
            .andExpect(jsonPath("$.[*].serviceFlag").value(hasItem(DEFAULT_SERVICE_FLAG.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getApp() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(app.getId().intValue()))
            .andExpect(jsonPath("$.commandLine").value(DEFAULT_COMMAND_LINE.toString()))
            .andExpect(jsonPath("$.serviceFlag").value(DEFAULT_SERVICE_FLAG.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getAllAppsByServiceFlagIsEqualToSomething() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where serviceFlag equals to DEFAULT_SERVICE_FLAG
        defaultAppShouldBeFound("serviceFlag.equals=" + DEFAULT_SERVICE_FLAG);

        // Get all the appList where serviceFlag equals to UPDATED_SERVICE_FLAG
        defaultAppShouldNotBeFound("serviceFlag.equals=" + UPDATED_SERVICE_FLAG);
    }

    @Test
    @Transactional
    public void getAllAppsByServiceFlagIsInShouldWork() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where serviceFlag in DEFAULT_SERVICE_FLAG or UPDATED_SERVICE_FLAG
        defaultAppShouldBeFound("serviceFlag.in=" + DEFAULT_SERVICE_FLAG + "," + UPDATED_SERVICE_FLAG);

        // Get all the appList where serviceFlag equals to UPDATED_SERVICE_FLAG
        defaultAppShouldNotBeFound("serviceFlag.in=" + UPDATED_SERVICE_FLAG);
    }

    @Test
    @Transactional
    public void getAllAppsByServiceFlagIsNullOrNotNull() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where serviceFlag is not null
        defaultAppShouldBeFound("serviceFlag.specified=true");

        // Get all the appList where serviceFlag is null
        defaultAppShouldNotBeFound("serviceFlag.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where status equals to DEFAULT_STATUS
        defaultAppShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the appList where status equals to UPDATED_STATUS
        defaultAppShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAppsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultAppShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the appList where status equals to UPDATED_STATUS
        defaultAppShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllAppsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the appList where status is not null
        defaultAppShouldBeFound("status.specified=true");

        // Get all the appList where status is null
        defaultAppShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppsByServerIsEqualToSomething() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);
        Server server = ServerResourceIT.createEntity(em);
        em.persist(server);
        em.flush();
        app.setServer(server);
        appRepository.saveAndFlush(app);
        Long serverId = server.getId();

        // Get all the appList where server equals to serverId
        defaultAppShouldBeFound("serverId.equals=" + serverId);

        // Get all the appList where server equals to serverId + 1
        defaultAppShouldNotBeFound("serverId.equals=" + (serverId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppShouldBeFound(String filter) throws Exception {
        restAppMockMvc.perform(get("/api/apps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId().intValue())))
            .andExpect(jsonPath("$.[*].commandLine").value(hasItem(DEFAULT_COMMAND_LINE.toString())))
            .andExpect(jsonPath("$.[*].serviceFlag").value(hasItem(DEFAULT_SERVICE_FLAG.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restAppMockMvc.perform(get("/api/apps/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppShouldNotBeFound(String filter) throws Exception {
        restAppMockMvc.perform(get("/api/apps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppMockMvc.perform(get("/api/apps/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingApp() throws Exception {
        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateApp() throws Exception {
        // Initialize the database
        appService.save(app);

        int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Update the app
        App updatedApp = appRepository.findById(app.getId()).get();
        // Disconnect from session so that the updates on updatedApp are not directly saved in db
        em.detach(updatedApp);
        updatedApp
            .commandLine(UPDATED_COMMAND_LINE)
            .serviceFlag(UPDATED_SERVICE_FLAG)
            .status(UPDATED_STATUS);

        restAppMockMvc.perform(put("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedApp)))
            .andExpect(status().isOk());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeUpdate);
        App testApp = appList.get(appList.size() - 1);
        assertThat(testApp.getCommandLine()).isEqualTo(UPDATED_COMMAND_LINE);
        assertThat(testApp.isServiceFlag()).isEqualTo(UPDATED_SERVICE_FLAG);
        assertThat(testApp.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingApp() throws Exception {
        int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Create the App

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppMockMvc.perform(put("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteApp() throws Exception {
        // Initialize the database
        appService.save(app);

        int databaseSizeBeforeDelete = appRepository.findAll().size();

        // Delete the app
        restAppMockMvc.perform(delete("/api/apps/{id}", app.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(App.class);
        App app1 = new App();
        app1.setId(1L);
        App app2 = new App();
        app2.setId(app1.getId());
        assertThat(app1).isEqualTo(app2);
        app2.setId(2L);
        assertThat(app1).isNotEqualTo(app2);
        app1.setId(null);
        assertThat(app1).isNotEqualTo(app2);
    }
}
