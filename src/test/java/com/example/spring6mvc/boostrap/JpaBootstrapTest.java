package com.example.spring6mvc.boostrap;

import com.example.spring6mvc.repositories.BeerRepository;
import com.example.spring6mvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class JpaBootstrapTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;
//
//    JpaBootstrap bootstrap;
//
//    @BeforeEach
//    void setUp() {
//        bootstrap = new JpaBootstrap(beerRepository,customerRepository);
//    }

//    @Test
//    void run() throws Exception {
//        bootstrap.run(null);
//        assertThat(beerRepository.findAll().size()).isEqualTo(10);
//        assertThat(customerRepository.findAll().size()).isEqualTo(10);
//    }
}