package com.example.spring6mvc.common;

public class Constant {
    public static final String PATH_WITH_ID = "/{id}";
    public static final String BEER_ROOT_PATH = "/api/v1/beers";
    public static final String BEER_PATH_DETAIL = BEER_ROOT_PATH + PATH_WITH_ID;

    public static final String CUSTOMER_ROOT_PATH = "/api/v1/customers";
    public static final String CUSTOMER_PATH_DETAIL = CUSTOMER_ROOT_PATH + PATH_WITH_ID;

}
