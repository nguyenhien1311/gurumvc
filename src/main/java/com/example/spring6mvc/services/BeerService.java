package com.example.spring6mvc.services;

import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Page<BeerDTO> getList(String beerName, BeerStyle style, Boolean showInventory, Integer pageNumber, Integer pageSize);
    Optional<BeerDTO> getById(UUID id);

    BeerDTO addBeer(BeerDTO beer);

    Optional<BeerDTO> updateBeer(UUID id, BeerDTO beer);
    Optional<BeerDTO> patchBeer(UUID id, BeerDTO beer);
    boolean deleteBeer(UUID id);
}
