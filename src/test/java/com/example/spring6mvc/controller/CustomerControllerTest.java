package com.example.spring6mvc.controller;

import com.example.spring6mvc.common.Constant;
import com.example.spring6mvc.exceptions.NotFoundException;
import com.example.spring6mvc.model.CustomerDTO;
import com.example.spring6mvc.services.CustomerService;
import com.example.spring6mvc.services.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    CustomerService service;

    @Captor
    ArgumentCaptor<UUID> captor;
    @Captor
    ArgumentCaptor<CustomerDTO> cusCaptor;
    CustomerServiceImpl impl;

    @BeforeEach
    void setUp() {
        impl = new CustomerServiceImpl();
    }

    @Test
    void getList() throws Exception {
        given(service.getList()).willReturn(impl.getList());

        mockMvc.perform(get(Constant.CUSTOMER_ROOT_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(impl.getList().size())));
    }

    @Test
    void testNot() throws Exception {
        given(service.getById(any(UUID.class))).willThrow(NotFoundException.class);

        mockMvc.perform(get(Constant.CUSTOMER_PATH_DETAIL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById() throws Exception {
        CustomerDTO customer = impl.getList().get(0);
        given(service.getById(customer.getId())).willReturn(Optional.of(customer));

        mockMvc.perform(get(Constant.CUSTOMER_PATH_DETAIL, customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(customer.getId().toString())));
    }

    @Test
    void handlePost() throws Exception {
        CustomerDTO customer = impl.getList().get(0);
        customer.setId(null);
        customer.setVersion(null);

        given(service.addCustomer(any(CustomerDTO.class))).willReturn(impl.getList().get(1));

        mockMvc.perform(post(Constant.CUSTOMER_ROOT_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void handlePut() throws Exception {
        CustomerDTO customer = impl.getList().get(0);
        given(service.updateCustomer(any(), any())).willReturn(Optional.of(customer));
        mockMvc.perform(put(Constant.CUSTOMER_PATH_DETAIL, customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());
        verify(service).updateCustomer(any(UUID.class), any(CustomerDTO.class));
    }

    @Test
    void handleDelete() throws Exception {
        CustomerDTO customer = impl.getList().get(0);
        given(service.deleteCustomer(any())).willReturn(true);
        mockMvc.perform(delete(Constant.CUSTOMER_PATH_DETAIL, customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteCustomer(captor.capture());

        assertThat(customer.getId()).isEqualTo(captor.getValue());
    }

    @Test
    void handlePatch() throws Exception {
        CustomerDTO customer = impl.getList().get(0);

        HashMap<String, String> map = new HashMap<>();
        map.put("customerName", "Nguoi dung moi");
        given(service.patchCustomer(any(), any())).willReturn(Optional.of(customer));
        mockMvc.perform(patch(Constant.CUSTOMER_PATH_DETAIL, customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(map)))
                .andExpect(status().isNoContent());

        verify(service).patchCustomer(captor.capture(), cusCaptor.capture());

        assertThat(customer.getId()).isEqualTo(captor.getValue());
        assertThat(map.get("customerName")).isEqualTo(cusCaptor.getValue().getCustomerName());
    }
}