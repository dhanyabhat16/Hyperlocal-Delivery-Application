package com.ecommerce.hyperlocaldelivery.util;

public class Constants {
    
    // API Status Codes
    public static final Integer STATUS_OK = 200;
    public static final Integer STATUS_CREATED = 201;
    public static final Integer STATUS_BAD_REQUEST = 400;
    public static final Integer STATUS_UNAUTHORIZED = 401;
    public static final Integer STATUS_FORBIDDEN = 403;
    public static final Integer STATUS_NOT_FOUND = 404;
    public static final Integer STATUS_CONFLICT = 409;
    public static final Integer STATUS_INTERNAL_ERROR = 500;
    
    // Messages
    public static final String MSG_SUCCESS = "Operation successful";
    public static final String MSG_ERROR = "Operation failed";
    public static final String MSG_INVALID_INPUT = "Invalid input provided";
    
    // API Endpoints
    public static final String API_BASE_PATH = "/api";
    public static final String CUSTOMERS_ENDPOINT = "/customers";
    public static final String PRODUCTS_ENDPOINT = "/products";
    public static final String CARTS_ENDPOINT = "/carts";
    public static final String ORDERS_ENDPOINT = "/orders";
}
