package com.example.spring6mvc.controller;

import com.example.spring6mvc.common.Constant;
import com.example.spring6mvc.exceptions.NotFoundException;
import com.example.spring6mvc.model.BeerDTO;
import com.example.spring6mvc.model.BeerStyle;
import com.example.spring6mvc.services.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(Constant.BEER_ROOT_PATH)
@RequiredArgsConstructor
public class BeerController {
    private final BeerService service;

    @GetMapping()
    public Page<BeerDTO> getList(@RequestParam(required = false) String beerName,
                                 @RequestParam(required = false) BeerStyle style,
                                 @RequestParam(required = false) Boolean showInventory,
                                 @RequestParam(required = false) Integer pageNumber,
                                 @RequestParam(required = false) Integer pageSize) {
        return service.getList(beerName, style, showInventory, pageNumber, pageSize);
    }

    @GetMapping(Constant.PATH_WITH_ID)
    public BeerDTO getBeer(@PathVariable("id") UUID id) {
        return service.getById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ResponseEntity addBeer(@Valid @RequestBody BeerDTO beer) {
        BeerDTO addBeer = service.addBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/" + addBeer.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping(Constant.PATH_WITH_ID)
    public ResponseEntity handlePut(@PathVariable("id") UUID id, @RequestBody BeerDTO beer) {
        Optional<BeerDTO> beerDTO = service.updateBeer(id, beer);
        if (beerDTO.isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(Constant.PATH_WITH_ID)
    public ResponseEntity deleteBeer(@PathVariable("id") UUID id) {
        boolean b = service.deleteBeer(id);
        if (!b) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity patchBeer(@PathVariable("id") UUID id, @RequestBody BeerDTO beer) {
        Optional<BeerDTO> beerDTO = service.patchBeer(id, beer);
        if (beerDTO.isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
