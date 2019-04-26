package com.pa.twb.web.rest.ext.attractiongrouptype;

import com.pa.twb.SmarttoursApp;
import com.pa.twb.domain.AttractionGroupType;
import com.pa.twb.repository.ext.ExtAttractionGroupTypeRepository;
import com.pa.twb.service.ext.ExtAttractionGroupTypeService;
import com.pa.twb.web.rest.TestUtil;
import com.pa.twb.web.rest.errors.ExceptionTranslator;
import com.pa.twb.web.rest.errors.ext.AttractionGroupTypeNotFoundException;
import com.pa.twb.web.rest.ext.ExtAttractionGroupTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.pa.twb.web.rest.TestUtil.createFormattingConversionService;
import static com.pa.twb.web.rest.ext.attractiongrouptype.AttractionGroupTypeDataUtil.createCreateAttractionGroupTypeEntityDTO;
import static com.pa.twb.web.rest.ext.attractiongrouptype.AttractionGroupTypeDataUtil.createUpdateAttractionGroupTypeEntityDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TODO: Update DTOs for relevant data and adjust tests for data accordingly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {SmarttoursApp.class}
)
@SuppressWarnings("unused")
public class ExtAttractionGroupTypeResourceIntTest {
    @Autowired
    private ExtAttractionGroupTypeRepository extAttractionGroupTypeRepository;

    @Autowired
    private ExtAttractionGroupTypeService extAttractionGroupTypeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAttractionGroupTypeMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ExtAttractionGroupTypeResource extAttractionGroupTypeResource = new ExtAttractionGroupTypeResource(extAttractionGroupTypeService);
        this.restAttractionGroupTypeMockMvc = MockMvcBuilders.standaloneSetup(extAttractionGroupTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    @Transactional
    public void testCreateAttractionGroupType() throws Exception {
        // some database setup

        int databaseSizeBeforeCreate = extAttractionGroupTypeRepository.findAll().size();
        this.restAttractionGroupTypeMockMvc.perform(post("/api/ext-attraction-group-type")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createCreateAttractionGroupTypeEntityDTO(1L)))) //update
            .andDo(print())
            .andExpect(status().isCreated());

        List<AttractionGroupType> list = extAttractionGroupTypeRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeCreate + 1);
        AttractionGroupType test = list.get(list.size() - 1);
        // assertThat(test.getName()).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    public void testCreateAttractionGroupTypeInvalidParent() {
        // some database setup

        int databaseSizeBeforeCreate = extAttractionGroupTypeRepository.findAll().size();
        assertThatThrownBy(() ->
            this.restAttractionGroupTypeMockMvc.perform(post("/api/ext-attraction-group-type")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(createCreateAttractionGroupTypeEntityDTO(Long.MAX_VALUE))))
                .andExpect(status().isCreated())).
            hasCause(new AttractionGroupTypeNotFoundException());

        List<AttractionGroupType> list = extAttractionGroupTypeRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void testUpdateAttractionGroupType() throws Exception {
        // some database setup

        int databaseSizeBeforeUpdate = extAttractionGroupTypeRepository.findAll().size();
        this.restAttractionGroupTypeMockMvc.perform(put("/api/ext-attraction-group-type")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createUpdateAttractionGroupTypeEntityDTO(1L)))) //update
            .andDo(print())
            .andExpect(status().isOk());

        List<AttractionGroupType> list = extAttractionGroupTypeRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeUpdate);
        AttractionGroupType test = list.get(list.size() - 1);
        // assertThat(test.getName()).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    public void testUpdateNonExistentAttractionGroupType() {
        int databaseSizeBeforeUpdate = extAttractionGroupTypeRepository.findAll().size();
        assertThatThrownBy(() ->
            this.restAttractionGroupTypeMockMvc.perform(put("/api/ext-attraction-group-type")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(createUpdateAttractionGroupTypeEntityDTO(Long.MAX_VALUE))))
                .andExpect(status().isOk())).
            hasCause(new AttractionGroupTypeNotFoundException());

        List<AttractionGroupType> list = extAttractionGroupTypeRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void testGetAttractionGroupType() throws Exception {
        // some database setup

        this.restAttractionGroupTypeMockMvc.perform(get("/api/ext-attraction-group-type/{id}", 1L)) //update
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(1L)); //update
        // .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    public void testGetNonExistentAttractionGroupType() {
        assertThatThrownBy(() ->
            this.restAttractionGroupTypeMockMvc.perform(get("/api/ext-attraction-group-type/{id}", Long.MAX_VALUE))
                .andExpect(status().isOk())).
            hasCause(new AttractionGroupTypeNotFoundException());
    }

    @Test
    @Transactional
    public void testGetAllAttractionGroupType() throws Exception {
        // some database setup

        this.restAttractionGroupTypeMockMvc.perform(get("/api/ext-attraction-group-type?sort=id,desc"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(1L))); //update
        // .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void testDeleteAttractionGroupType() throws Exception {
        // some database setup

        int databaseSizeBeforeDelete = extAttractionGroupTypeRepository.findAll().size();
        this.restAttractionGroupTypeMockMvc.perform(delete("/api/ext-attraction-group-type/{id}", 1L))
            .andDo(print())
            .andExpect(status().isNoContent());

        List<AttractionGroupType> list = extAttractionGroupTypeRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeDelete - 1);

        Optional<AttractionGroupType> test = extAttractionGroupTypeRepository.findById(1L); // update
        assertThat(test.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testDeleteNonExistentAttractionGroupType() {
        assertThatThrownBy(() ->
            this.restAttractionGroupTypeMockMvc.perform(delete("/api/ext-attraction-group-type/{id}", Long.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent())).
            hasCause(new AttractionGroupTypeNotFoundException());
    }

    @Test
    @Transactional
    public void testGetAllDeletedAttractionGroupType() throws Exception {
        // some database setup

        this.restAttractionGroupTypeMockMvc.perform(get("/api/ext-attraction-group-type/deleted?sort=id,desc"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(1L))); //update
        // .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void testRecoverDeletedAttractionGroupType() throws Exception {
        // some database setup

        this.restAttractionGroupTypeMockMvc.perform(post("/api/ext-attraction-group-type/recover/{id}", 1L)) // update
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(1L)); //update
        // .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    public void testRecoverNonExistentAttractionGroupType() {
        assertThatThrownBy(() ->
            this.restAttractionGroupTypeMockMvc.perform(post("/api/ext-attraction-group-type/recover/{id}", Long.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isOk())).
            hasCause(new AttractionGroupTypeNotFoundException());
    }
}