package com.example.spring6mvc.mapper;

import com.example.spring6mvc.entities.Customer;
import com.example.spring6mvc.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDTO customerToDTO(Customer customer);

    Customer customerDtoToCustomer(CustomerDTO dto);
}
