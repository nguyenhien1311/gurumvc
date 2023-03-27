package com.example.spring6mvc.services;

import com.example.spring6mvc.mapper.CustomerMapper;
import com.example.spring6mvc.model.CustomerDTO;
import com.example.spring6mvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getList() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToDTO)
                .toList();
    }

    @Override
    public Optional<CustomerDTO> getById(UUID id) {
        return Optional.ofNullable(customerMapper.customerToDTO(customerRepository.findById(id).orElse(null)));
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO customer) {
        return customerMapper.customerToDTO(customerRepository.save(customerMapper.customerDtoToCustomer(customer)));
    }

    @Override
    public Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> reference = new AtomicReference<>();
        customerRepository.findById(id).ifPresentOrElse(customer1 -> {
            customer1.setCustomerName(customer.getCustomerName());
            reference.set(Optional.of(customerMapper.customerToDTO(customerRepository.save(customer1))));
        }, () -> {
            reference.set(Optional.empty());
        });
        return reference.get();
    }

    @Override
    public Optional<CustomerDTO> patchCustomer(UUID id, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> reference = new AtomicReference<>();
        customerRepository.findById(id).ifPresentOrElse(customer1 -> {
            if (StringUtils.hasLength(customer.getCustomerName())) {
                customer1.setCustomerName(customer.getCustomerName());
            }
            reference.set(Optional.of(customerMapper.customerToDTO(customerRepository.save(customer1))));
        }, () -> {
            reference.set(Optional.empty());
        });
        return reference.get();
    }

    @Override
    public boolean deleteCustomer(UUID id) {
        if (getById(id).isPresent()) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
