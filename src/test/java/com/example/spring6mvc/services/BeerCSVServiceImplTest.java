package com.example.spring6mvc.services;

import com.example.spring6mvc.model.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
class BeerCSVServiceImplTest {

    BeerCSVService service = new BeerCSVServiceImpl();
    @Test
    void convertCsv() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csv/beers.csv");
        List<BeerCSVRecord> beerCSVRecords = service.convertCsv(file);
        System.out.println(beerCSVRecords.get(1).getCount());

        assertThat(beerCSVRecords.size()).isGreaterThan(0);
    }
}