package com.example.spring6mvc.boostrap;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.entities.Customer;
import com.example.spring6mvc.model.BeerStyle;
import com.example.spring6mvc.repositories.BeerRepository;
import com.example.spring6mvc.repositories.CustomerRepository;
import com.example.spring6mvc.services.BeerCSVService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class JpaBootstrap implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCSVService service;

    @Override
    public void run(String... args) throws Exception {
        addBeers();
        addCustomers();
        addBeersCsv();
    }

    private void addBeersCsv() throws FileNotFoundException {
        if (beerRepository.count() <= 10) {
            File file = ResourceUtils.getFile("classpath:csv/beers.csv");
            List<Beer> beers = service.convertCsv(file).stream()
                    .map(record -> {
                        BeerStyle style = switch (record.getStyle()) {
                            case "American Pale Lager" -> BeerStyle.LAGER;
                            case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                                    BeerStyle.ALE;
                            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                            case "Baltic Porter", "American Porter" -> BeerStyle.PORTER;
                            case "American Stout", "Oatmeal Stout", "Milk / Sweet Stout" -> BeerStyle.STOUT;
                            case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                            case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                            case "English Pale Ale" -> BeerStyle.PALE_ALE;
                            default -> BeerStyle.PILSNER;
                        };
                        return Beer.builder()
                                .beerStyle(style)
                                .beerName(StringUtils.abbreviate(record.getBeer(), 50))
                                .price(BigDecimal.TEN)
                                .upc(record.getCount().toString())
                                .quantityOnHand(record.getCount())
                                .build();
                    })
                    .toList();
            beerRepository.saveAllAndFlush(beers);
        }
    }

    void addBeers() {
        while (beerRepository.count() < 10) {
            ArrayList<Beer> beers = new ArrayList<>();
            IntStream.range(0, 10).forEach(operand -> {
                beers.add(Beer.builder()
                        .beerName("Beer " + operand)
                        .quantityOnHand(10)
                        .price(BigDecimal.valueOf(2000000))
                        .beerStyle(BeerStyle.WHEAT)
                        .upc("i dont know")
                        .build());
            });
            beerRepository.saveAll(beers);
        }
    }

    void addCustomers() {
        while (customerRepository.count() < 10) {
            ArrayList<Customer> customers = new ArrayList<>();
            IntStream.range(0, 10).forEach(value -> {
                customers.add(Customer.builder()
                        .customerName("Nguoi dung " + value)
                        .build());
            });
            customerRepository.saveAll(customers);
        }
    }
}
