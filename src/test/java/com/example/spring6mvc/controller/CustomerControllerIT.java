package com.example.spring6mvc.controller;

import com.example.spring6mvc.entities.Customer;
import com.example.spring6mvc.exceptions.NotFoundException;
import com.example.spring6mvc.mapper.CustomerMapper;
import com.example.spring6mvc.model.CustomerDTO;
import com.example.spring6mvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper mapper;

    final static String NEW_NAME = "New Customer Name";

    @Test
    void getList() {
        List<CustomerDTO> list = customerController.getList();

        assertThat(list.size()).isEqualTo(10);
    }

    @Rollback
    @Transactional
    @Test
    void getEmptyList() {
        customerRepository.deleteAll();
        List<CustomerDTO> list = customerController.getList();

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    void getById() {
        Customer customer = customerRepository.findAll().get(0);

        CustomerDTO dto = customerController.getById(customer.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    void getByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getById(UUID.randomUUID());
        });
    }

    @Transactional
    @Rollback
    @Test
    void handlePost() {
        CustomerDTO dto = CustomerDTO.builder().customerName(NEW_NAME).build();
        ResponseEntity responseEntity = customerController.handlePost(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] split = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID uuid = UUID.fromString(split[1]);
        Customer customer = customerRepository.findById(uuid).get();
        assertThat(customer.getId()).isNotNull();
    }

    @Transactional
    @Rollback
    @Test
    void handlePut() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = mapper.customerToDTO(customer);
        customerDTO.setCustomerName(NEW_NAME);
        customerDTO.setId(null);
        customerDTO.setVersion(null);
        ResponseEntity responseEntity = customerController.handlePut(customer.getId(), customerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        assertThat(customerRepository.findById(customer.getId()).get().getCustomerName()).isEqualTo(NEW_NAME);
    }

    @Test
    void handlePutNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.handlePut(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }

    @Transactional
    @Rollback
    @Test
    void handleDelete() {
        Customer customer = customerRepository.findAll().get(0);
        ResponseEntity responseEntity = customerController.handleDelete(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));

        assertThat(customerRepository.findById(customer.getId()).isEmpty());
    }

    @Test
    void handleDeleteNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.handleDelete(UUID.randomUUID());
        });
    }

    @Transactional
    @Rollback
    @Test
    void handlePatch() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = mapper.customerToDTO(customer);
        dto.setCustomerName(NEW_NAME);
        dto.setId(null);
        dto.setVersion(null);
        ResponseEntity responseEntity = customerController.handlePatch(customer.getId(), dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        assertThat(customerRepository.findById(customer.getId()).get().getCustomerName()).isEqualTo(NEW_NAME);

    }

    @Test
    void handlePatchNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.handlePatch(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }
}