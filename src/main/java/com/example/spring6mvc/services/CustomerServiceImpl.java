package com.example.spring6mvc.services;

import com.example.spring6mvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    Map<UUID, CustomerDTO> data;

    public CustomerServiceImpl() {
        this.data = new HashMap<>();

        IntStream.range(0, 3).forEachOrdered(value -> {
            CustomerDTO customer = CustomerDTO.builder()
                    .id(UUID.randomUUID())
                    .customerName("Customer " + value)
                    .version(123)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
            data.put(customer.getId(), customer);
        });
    }

    @Override
    public List<CustomerDTO> getList() {
        return new ArrayList<>(data.values());
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO customer) {
        CustomerDTO customer1 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName(customer.getCustomerName())
                .version(customer.getVersion())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
        data.put(customer1.getId(), customer1);
        return customer1;
    }

    @Override
    public Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customer) {
        CustomerDTO exCus = data.get(id);
        exCus.setCustomerName(customer.getCustomerName());
        data.put(id, exCus);
        return Optional.of(exCus);
    }

    @Override
    public boolean deleteCustomer(UUID id) {
        if (data.get(id) != null) {
            data.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchCustomer(UUID id, CustomerDTO customer) {
        CustomerDTO exCus = data.get(id);
        if (StringUtils.hasLength(customer.getCustomerName())) {
            exCus.setCustomerName(customer.getCustomerName());
        }
        if (customer.getVersion() != null) {
            exCus.setVersion(customer.getVersion());
        }
        data.put(exCus.getId(), exCus);
        return Optional.of(exCus);
    }

    @Override
    public Optional<CustomerDTO> getById(UUID id) {
        return Optional.of(data.get(id));
    }
}
