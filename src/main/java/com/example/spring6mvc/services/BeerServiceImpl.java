package com.example.spring6mvc.services;

import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {
    HashMap<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        IntStream.range(0, 10).forEach(value -> {
            BeerDTO beer = BeerDTO.builder()
                    .id(UUID.randomUUID())
                    .version(1)
                    .beerName("Beer hanoi " + value)
                    .beerStyle(BeerStyle.WHEAT)
                    .upc("123455 " + value)
                    .price(new BigDecimal("123" + value))
                    .quantityOnHand(123 + value)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            beerMap.put(beer.getId(), beer);
        });
    }

    @Override
    public Optional<BeerDTO> updateBeer(UUID id, BeerDTO beer) {
        BeerDTO exBeer = beerMap.get(id);
        exBeer.setBeerName(beer.getBeerName());
        exBeer.setBeerStyle(beer.getBeerStyle());
        beerMap.put(id, exBeer);
        return Optional.of(exBeer);
    }

    @Override
    public Page<BeerDTO> getList(String beerName, BeerStyle style, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(new ArrayList<>(beerMap.values()));
    }

    @Override
    public BeerDTO addBeer(BeerDTO beer) {
        BeerDTO beer1 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Beer sai gon ")
                .beerStyle(BeerStyle.ALE)
                .upc("123457")
                .price(new BigDecimal("999"))
                .quantityOnHand(999)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        beerMap.put(beer1.getId(), beer1);
        return beer1;
    }

    @Override
    public Optional<BeerDTO> getById(UUID id) {
        log.debug("Run into getByID BeerServiceImpl");
        return Optional.of(beerMap.get(id));
    }

    @Override
    public Optional<BeerDTO> patchBeer(UUID id, BeerDTO beer) {
        return Optional.empty();
    }

    @Override
    public boolean deleteBeer(UUID id) {
        if (beerMap.get(id) != null){
            beerMap.remove(id);
            return true;
        }
        return false;
    }
}
