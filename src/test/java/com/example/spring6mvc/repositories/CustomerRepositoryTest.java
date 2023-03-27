package com.example.spring6mvc.repositories;

import com.example.spring6mvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository repository;

    @Test
    void testAdd() {
        Customer customer = repository.save(Customer.builder().customerName("Hien").build());

        assertThat(customer).isNotNull();
    }
}