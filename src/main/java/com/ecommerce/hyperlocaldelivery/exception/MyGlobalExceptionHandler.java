package com.ecommerce.hyperlocaldelivery.exception;

import com.ecommerce.hyperlocaldelivery.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//this is for meaningful error messages

@RestControllerAdvice //intercepts any exception thrown
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)//whenever the validation fails
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidExcpetion(MethodArgumentNotValidException e){
        Map<String,String> response=new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{//get all validation errors
            String fieldName=((FieldError)err).getField();//get the field name out from the error message
            String message=err.getDefaultMessage();
            response.put(fieldName,message);//store the extracted fields in the map
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    //handles resource not found cases
    @ExceptionHandler(myResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(myResourceNotFoundException e){

        String message=e.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIExceptions.class)
    public ResponseEntity<APIResponse> APIExceptions(APIExceptions e){
        String message=e.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
