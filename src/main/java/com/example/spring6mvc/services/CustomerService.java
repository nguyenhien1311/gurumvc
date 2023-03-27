package com.example.spring6mvc.services;

import com.example.spring6mvc.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getList();

    Optional<CustomerDTO> getById(UUID id);

    CustomerDTO addCustomer(CustomerDTO customer);

    Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customer);

    Optional<CustomerDTO> patchCustomer(UUID id, CustomerDTO customer);
    boolean deleteCustomer(UUID id);
}
