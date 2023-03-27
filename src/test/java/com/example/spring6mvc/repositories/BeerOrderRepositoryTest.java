package com.example.spring6mvc.repositories;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.entities.BeerOrder;
import com.example.spring6mvc.entities.BeerOrderShipment;
import com.example.spring6mvc.entities.Customer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class BeerOrderRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
            BeerOrderRepository beerOrderRepository;
    Beer beer;
    Customer customer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().get(0);
        customer = customerRepository.findAll().get(0);
    }

    @Test
    @Transactional
    @Rollback
    void getData() {
        BeerOrder order = BeerOrder.builder()
                .customer(customer)
                .customerRef("Customer refff")
                .orderShipment(BeerOrderShipment.builder()
                        .trackingNumber("Zyx-12003-asd")
                        .build())
                .build();
        BeerOrder beerOrder = beerOrderRepository.saveAndFlush(order);
        System.out.println(beerOrder.getCustomerRef());
    }
}