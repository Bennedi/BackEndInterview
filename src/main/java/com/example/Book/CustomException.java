package com.example.Book;

import java.util.List;

public class CustomException extends RuntimeException {

    private final List<String> errors;

    public CustomException(List<String> errors) {
        super(errors.toString()); // Optional: Use this if you need to keep the message for logging
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
