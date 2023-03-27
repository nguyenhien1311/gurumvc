package com.example.spring6mvc.repositories;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.entities.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BeerRepository beerRepository;
    Beer beer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().get(0);
    }

    @Transactional
    @Test
    void getCategory() {
        Category save = categoryRepository.save(Category.builder()
                .description("Simple Description")
                .build());
        beer.addCategory(save);
        Beer savedBeer = beerRepository.save(beer);
        System.out.println(savedBeer.getCategories().size());
    }
}