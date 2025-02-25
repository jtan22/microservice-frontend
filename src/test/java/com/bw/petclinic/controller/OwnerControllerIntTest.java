package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OwnerControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFindOwner() throws Exception {
        mockMvc
                .perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attribute("owner", new Owner()));
    }

    @Test
    public void testListOwnerDefault() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Owner> ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(5, ownerPage.getContent().size());
        assertEquals(10, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerDavis() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Owner> ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(2, ownerPage.getContent().size());
        assertEquals(2, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerNotFound() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Dav"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attributeExists("owner"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals("Dav", owner.getLastName());
    }

    @Test
    public void testShowOwner() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners/6"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals(6, owner.getId());
        assertEquals(2, owner.getPets().size());
        assertEquals(2, owner.getPets().get(0).getVisits().size());
    }

    @Test
    public void testNewOwner() throws Exception {
        mockMvc
                .perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testAddOwner() throws Exception {
        ModelAndView mav = mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "City")
                        .param("telephone", "0123456789"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();
        assertNotNull(mav);
        String viewName = mav.getViewName();
        assertNotNull(viewName);
        assertTrue(viewName.startsWith("redirect:/owners/"));
        String savedOwnerId = viewName.substring(viewName.lastIndexOf("/") + 1);
        jdbcTemplate.update("delete from owners where id = " + savedOwnerId);
    }

    @Test
    public void testAddOwnerInvalidCity() throws Exception {
        mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "")
                        .param("telephone", "0123456789"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attributeHasFieldErrors("owner", "city"));
    }

    @Test
    public void testAddOwnerInvalidTelephone() throws Exception {
        mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "City")
                        .param("telephone", "Telephone"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "OwnerService.add failed [400 : \"Bad request [Owner telephone must be 10 digits]\"]"));
    }

}
