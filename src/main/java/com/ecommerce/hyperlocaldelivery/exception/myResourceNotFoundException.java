package com.ecommerce.hyperlocaldelivery.exception;

//custom exception class to throw meaningful exceptions
public class myResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public myResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with  %s: %s",resourceName,field,fieldName)); //this sets an exception message which can be later retreived from e.getMessage() by writing msg to the parent
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public myResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with  %s: %d",resourceName,field,fieldId));

        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public myResourceNotFoundException() {
    }
}
