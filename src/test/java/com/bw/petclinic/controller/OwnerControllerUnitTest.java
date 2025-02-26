package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.exception.PetClinicServiceException;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import com.bw.petclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @MockBean
    private VisitService visitService;

    @Test
    public void testFindOwner() throws Exception {
        mockMvc
                .perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attribute("owner", new Owner()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListOwnerDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Owner owner = new Owner();
        owner.setId(1);
        Page<Owner> ownerPage = new PageImpl<>(List.of(owner, owner, owner, owner, owner), pageable, 10);
        when(ownerService.findAll(0, 5, null)).thenReturn(ownerPage);
        when(petService.findByOwnerId(1)).thenReturn(List.of(new Pet()));
        ModelAndView mav = mockMvc
                .perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(5, ownerPage.getContent().size());
        assertEquals(10, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListOwnerDavis() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Owner owner = new Owner();
        owner.setId(1);
        Page<Owner> ownerPage = new PageImpl<>(List.of(owner, owner), pageable, 2);
        when(ownerService.findAll(0, 5, "Davis")).thenReturn(ownerPage);
        when(petService.findByOwnerId(1)).thenReturn(List.of(new Pet()));
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(2, ownerPage.getContent().size());
        assertEquals(2, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerNotFound() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Owner> ownerPage = new PageImpl<>(List.of(), pageable, 0);
        when(ownerService.findAll(0, 5, "Dav")).thenReturn(ownerPage);
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
        Pet pet = new Pet();
        pet.setId(7);
        Owner owner = new Owner();
        owner.setId(6);
        when(ownerService.findById(6)).thenReturn(owner);
        when(petService.findByOwnerId(6)).thenReturn(List.of(pet));
        when(visitService.findByPetId(7)).thenReturn(List.of(new Visit(), new Visit()));
        ModelAndView mav = mockMvc
                .perform(get("/owners/6"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner loadedOwner = (Owner) mav.getModel().get("owner");
        assertEquals(6, loadedOwner.getId());
        assertEquals(1, loadedOwner.getPets().size());
        assertEquals(2, loadedOwner.getPets().get(0).getVisits().size());
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
        Owner owner = createOwner();
        when(ownerService.add(owner)).thenReturn(owner);
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
        when(ownerService.add(any(Owner.class)))
                .thenThrow(new PetClinicServiceException(
                        "OwnerService.add failed [400 : \"Bad request [Owner telephone must be 10 digits]\"]"));
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

    @Test
    public void testEditOwner() throws Exception {
        Owner owner = createGeorge();
        owner.setId(1);
        when(ownerService.findById(1)).thenReturn(owner);
        mockMvc
                .perform(get("/owners/edit?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attribute("owner", owner));
    }

    @Test
    public void testUpdateOwner() throws Exception {
        mockMvc
                .perform(post("/owners/edit?ownerId=1")
                        .param("id", "1")
                        .param("firstName", "George")
                        .param("lastName", "Franklin")
                        .param("address", "110 W. Liberty St.")
                        .param("city", "Randwick")
                        .param("telephone", "6085551023"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }

    private Owner createGeorge() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        return owner;
    }

    private Owner createOwner() {
        Owner owner = new Owner();
        owner.setFirstName("First");
        owner.setLastName("Last");
        owner.setAddress("Address");
        owner.setCity("City");
        owner.setTelephone("0123456789");
        return owner;
    }

}
