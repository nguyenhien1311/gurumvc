package com.example.spring6mvc.controller;

import com.example.spring6mvc.common.Constant;
import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.services.BeerService;
import com.example.spring6mvc.services.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WebMvcTest(BeerController.class)
class BeerControllerTest {
    @Autowired
    MockMvc mock;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BeerService service;

    BeerServiceImpl impl;

    @BeforeEach
    void setUp() {
        impl = new BeerServiceImpl();
    }

    @Test
    void getBeers() throws Exception {
        given(service.getList(any(), any(), any(), any(), any())).willReturn(impl.getList(null, null, false, 1, 25));
        mock.perform(get(Constant.BEER_ROOT_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(10)));
    }

    @Test
    void getBeerById() throws Exception {
        BeerDTO beer = impl.getList(null, null, false, 1, 25).getContent().get(0);
        given(service.getById(beer.getId())).willReturn(Optional.of(beer));
        mock.perform(get(Constant.BEER_PATH_DETAIL, beer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

    @Test
    void addBeer() throws Exception {
        BeerDTO beer = BeerDTO.builder().build();

        given(service.addBeer(any(BeerDTO.class))).willReturn(impl.getList(null, null, false, 1, 25).getContent().get(1));
        MvcResult result = mock.perform(post(Constant.BEER_ROOT_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void handlePut() throws Exception {
        BeerDTO beer = impl.getList(null, null, false, 1, 25).getContent().get(0);
        given(service.updateBeer(any(),any())).willReturn(Optional.of(beer));
        mock.perform(put(Constant.BEER_PATH_DETAIL, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());
        verify(service).updateBeer(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void deleteBeer() throws Exception {
        BeerDTO beer = impl.getList(null, null, false, 1, 25).getContent().get(0);
        given(service.deleteBeer(any())).willReturn(true);
        mock.perform(delete(Constant.BEER_PATH_DETAIL, beer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(service).deleteBeer(captor.capture());

        assertThat(beer.getId()).isEqualTo(captor.getValue());
    }
}