package com.example.spring6mvc.controller;

import com.example.spring6mvc.common.Constant;
import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.exceptions.NotFoundException;
import com.example.spring6mvc.mapper.BeerMapper;
import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {

    static final String NEW_NAME = "New Beer Name";
    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerMapper mapper;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objMapper;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testSearch() throws Exception {
        mvc.perform(get(Constant.BEER_ROOT_PATH)
                        .queryParam("beerName", "ale")
                        .queryParam("style", "ALE")
                        .queryParam("showInventory", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(11))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testSearchFalse() throws Exception {
        mvc.perform(get(Constant.BEER_ROOT_PATH)
                        .queryParam("beerName", "ale")
                        .queryParam("style", "ALE")
                        .queryParam("showInventory", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(11))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void getBeersWithParams() throws Exception {
        mvc.perform(get(Constant.BEER_ROOT_PATH)
                        .queryParam("beerName", "Ale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(11)));
    }

    @Test
    void handlePutNameInvalid() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        beer.setBeerName("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        MvcResult result = mvc.perform(put(Constant.BEER_PATH_DETAIL, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void getList() {
        Page<BeerDTO> list = beerController.getList(any(), any(), any(), any(), any());

        assertThat(list.getContent().size()).isEqualTo(25);
    }

    @Rollback
    @Transactional
    @Test
    void getListEmpty() {
        beerRepository.deleteAll();
        Page<BeerDTO> list = beerController.getList(null, null, null, 1, 25);

        assertThat(list.getContent().size()).isEqualTo(0);
    }

    @Test
    void getBeer() {
        Beer beer = beerRepository.findAll().get(0);

        BeerDTO beer1 = beerController.getBeer(beer.getId());

        assertThat(beer1.getId()).isNotNull();
    }

    @Test
    void getBeerNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeer(UUID.randomUUID());
        });
    }

    @Transactional
    @Rollback
    @Test
    void addBeer() {
        BeerDTO newBeer = BeerDTO.builder().build();

        ResponseEntity responseEntity = beerController.addBeer(newBeer);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));

        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] split = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID uuid = UUID.fromString(split[1]);

        Beer beer = beerRepository.findById(uuid).get();
        assertThat(beer).isNotNull();
    }

    @Transactional
    @Rollback
    @Test
    void handlePut() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = mapper.beerToBeerDTO(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        beerDTO.setBeerName(NEW_NAME);
        ResponseEntity responseEntity = beerController.handlePut(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        BeerDTO dto = beerController.getBeer(beer.getId());
        assertThat(dto.getBeerName()).isEqualTo(NEW_NAME);
    }

//    @Transactional
//    @Rollback
    @Test
    void handlePutBadVersion() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = mapper.beerToBeerDTO(beer);

        beerDTO.setBeerName("Update lan 1");
        MvcResult result = mvc.perform(put(Constant.BEER_PATH_DETAIL, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        beerDTO.setBeerName("Update lan 2");
        MvcResult result2 = mvc.perform(put(Constant.BEER_PATH_DETAIL, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent())
                .andReturn();
        System.out.println(result2.getResponse().getStatus());
    }

    @Test
    void handlePutNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.handlePut(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Transactional
    @Rollback
    @Test
    void testPatchBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = mapper.beerToBeerDTO(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        beerDTO.setBeerName(NEW_NAME);
        ResponseEntity responseEntity = beerController.patchBeer(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));

    }

    @Test
    void testPatchNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteBeer(UUID.randomUUID());
        });
    }

    @Transactional
    @Rollback
    @Test
    void deleteBeer() {
        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity responseEntity = beerController.deleteBeer(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));

        assertThat(beerRepository.findById(beer.getId()).isEmpty());
    }

    @Test
    void deleteBeerNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteBeer(UUID.randomUUID());
        });
    }
}