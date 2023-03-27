package com.example.spring6mvc.mapper;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDTO(Beer beer);
}
