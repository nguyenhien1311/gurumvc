package com.example.spring6mvc.controller;

import com.example.spring6mvc.common.Constant;
import com.example.spring6mvc.exceptions.NotFoundException;
import com.example.spring6mvc.model.CustomerDTO;
import com.example.spring6mvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(Constant.CUSTOMER_ROOT_PATH)
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @GetMapping()
    public List<CustomerDTO> getList() {
        return service.getList();
    }

    @GetMapping(Constant.PATH_WITH_ID)
    public CustomerDTO getById(@PathVariable("id") UUID uuid) {
        return service.getById(uuid).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ResponseEntity handlePost(@RequestBody CustomerDTO customer) {
        CustomerDTO addCustomer = service.addCustomer(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/" + addCustomer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping(Constant.PATH_WITH_ID)
    public ResponseEntity handlePut(@PathVariable("id") UUID id, @RequestBody CustomerDTO customer) {
        Optional<CustomerDTO> customerDTO = service.updateCustomer(id, customer);
        if (customerDTO.isEmpty()){
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(Constant.PATH_WITH_ID)
    public ResponseEntity handleDelete(@PathVariable("id") UUID id) {
        boolean b = service.deleteCustomer(id);
        if (!b){
            throw  new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(Constant.PATH_WITH_ID)
    public ResponseEntity handlePatch(@PathVariable("id") UUID id, @RequestBody CustomerDTO customer) {
        Optional<CustomerDTO> customerDTO = service.patchCustomer(id, customer);
        if (customerDTO.isEmpty()){
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
