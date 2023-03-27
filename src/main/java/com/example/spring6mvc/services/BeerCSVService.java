package com.example.spring6mvc.services;

import com.example.spring6mvc.entities.Beer;
import com.example.spring6mvc.model.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCSVService {
    List<BeerCSVRecord> convertCsv(File file);
}
