package com.ecommerce.hyperlocaldelivery.config;

import java.util.List;

public class AppConstant {
    public static final String PAGE_NUMBER="0";
    public static final String PAGE_SIZE="20";
    public static final String SORT_BY="categoryId";
    public static final String SORT_ORDER="ascending";

    public static final List<String> SUPPORTED_CITIES = List.of(
        "Bangalore",
        "Chennai",
        "Hyderabad",
        "Mumbai"
    );
}
