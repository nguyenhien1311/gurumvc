package com.example.spring6mvc.services;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.mapper.BeerMapper;
import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.model.BeerStyle;
import com.example.spring6mvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class BeerServiceJPA implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;

    @Override
    public Page<BeerDTO> getList(String beerName, BeerStyle style, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        Page<Beer> list;
        if (StringUtils.hasLength(beerName) && style == null) {
            list = findByName(beerName, pageRequest);
        } else if (!StringUtils.hasLength(beerName) && style != null) {
            list = findByStyle(style, pageRequest);
        } else if (StringUtils.hasLength(beerName) && style != null) {
            list = findByNameStyle(beerName, style, pageRequest);
        } else {
            list = beerRepository.findAll(pageRequest);
        }

        if (showInventory != null && !showInventory) {
            list.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return list.map(beerMapper::beerToBeerDTO);
    }

    PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("quantityOnHand"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    Page<Beer> findByName(String name, PageRequest pageRequest) {
        return beerRepository.findAllByBeerNameLikeIgnoreCase("%" + name + "%", pageRequest);
    }

    Page<Beer> findByStyle(BeerStyle style, PageRequest pageRequest) {
        return beerRepository.findAllByBeerStyle(style, pageRequest);
    }

    Page<Beer> findByNameStyle(String name, BeerStyle style, PageRequest pageRequest) {
        return beerRepository.findAllByBeerNameLikeIgnoreCaseAndBeerStyle("%" + name + "%", style, pageRequest);
    }

    @Override
    public Optional<BeerDTO> getById(UUID id) {
        return Optional.ofNullable(beerMapper.beerToBeerDTO(beerRepository.findById(id).orElse(null)));
    }

    @Override
    public BeerDTO addBeer(BeerDTO beer) {
        return beerMapper.beerToBeerDTO(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
    }

    @Override
    public Optional<BeerDTO> updateBeer(UUID id, BeerDTO beer) {
        return Optional.of(beerMapper.beerToBeerDTO(beerRepository.save(beerMapper.beerDtoToBeer(beer))));

//        AtomicReference<Optional<BeerDTO>> referenc = new AtomicReference<>();
//        beerRepository.findById(id).ifPresentOrElse(beer1 -> {
//            beer1.setBeerName(beer.getBeerName());
//            beer1.setBeerStyle(beer.getBeerStyle());
//            beer1.setUpc(beer.getUpc());
//            beer1.setPrice(beer.getPrice());
//            beer1.setVersion(beer1.getVersion());
//            referenc.set(Optional.of(beerMapper.beerToBeerDTO(beerRepository.save(beer1))));
//        }, () -> {
//            referenc.set(Optional.empty());
//        });
//        return referenc.get();

    }

    @Override
    public Optional<BeerDTO> patchBeer(UUID id, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> reference = new AtomicReference<>();
        beerRepository.findById(id).ifPresentOrElse(beer1 -> {
            if (StringUtils.hasLength(beer.getBeerName())) {
                beer1.setBeerName(beer.getBeerName());
            }
            if (StringUtils.hasLength(beer.getUpc())) {
                beer1.setUpc(beer.getUpc());
            }
            if (beer.getQuantityOnHand() != null) {
                beer1.setQuantityOnHand(beer.getQuantityOnHand());
            }
            if (beer.getPrice() != null) {
                beer1.setPrice(beer.getPrice());
            }
            reference.set(Optional.of(beerMapper.beerToBeerDTO(beerRepository.save(beer1))));
        }, () -> {
            reference.set(Optional.empty());
        });
        return reference.get();
    }

    @Override
    public boolean deleteBeer(UUID id) {
        if (beerRepository.findById(id).isPresent()) {
            beerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
