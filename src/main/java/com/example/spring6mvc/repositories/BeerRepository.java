package com.example.spring6mvc.repositories;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.model.BeerStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    Page<Beer> findAllByBeerNameLikeIgnoreCase(String name, Pageable pageable);

    Page<Beer> findAllByBeerStyle(BeerStyle style, Pageable pageable);

    Page<Beer> findAllByBeerNameLikeIgnoreCaseAndBeerStyle(String name , BeerStyle style, Pageable pageable);
}
