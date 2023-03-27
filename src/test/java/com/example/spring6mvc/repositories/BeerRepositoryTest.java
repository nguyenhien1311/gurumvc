package com.example.spring6mvc.repositories;

import com.example.spring6mvc.boostrap.JpaBootstrap;
import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.model.BeerStyle;
import com.example.spring6mvc.services.BeerCSVServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
@Import({JpaBootstrap.class, BeerCSVServiceImpl.class})
public class BeerRepositoryTest {

    @Autowired
    BeerRepository repository;

    @Test
    void testAdd() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.save(Beer.builder()
                    .beerName("Beer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer")
                    .beerStyle(BeerStyle.STOUT)
                    .upc("asas")
                    .price(BigDecimal.valueOf(11.99))
                    .build());
            repository.flush();
        });
//        Beer beer = repository.save(Beer.builder().beerName("Beer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tuiBeer cua tui")
//                        .beerStyle(BeerStyle.STOUT)
//                        .upc("asas")
//                        .price(BigDecimal.valueOf(11.99))
//                .build());
//        repository.flush();
//        assertThat(beer).isNotNull();
//        assertThat(beer.getId()).isNotNull();
//        assertThat(beer.getBeerName()).isNotNull();
    }

    @Test
    void findAllByBeerNameIsIgnoreCase() {
        Page<Beer> list = repository.findAllByBeerNameLikeIgnoreCase("%Ale%", null);

         assertThat(list.getContent().size()).isEqualTo(636 );
    }
    @Test
    void findAllByBeerStyle() {
        Page<Beer> list = repository.findAllByBeerStyle(BeerStyle.STOUT, null);

         assertThat(list.getContent().size()).isEqualTo(67 );
    }
    @Test
    void findAllByNameAndBeerStyle() {
        Page<Beer> list = repository.findAllByBeerNameLikeIgnoreCaseAndBeerStyle("%ale%",BeerStyle.ALE, null);

         assertThat(list.getContent().size()).isEqualTo(251 );
    }
}
