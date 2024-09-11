package com.example.Book;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FieldValidator {

    public void validateTitle(String title) {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isEmpty()) {
            errors.add("Title cannot be null or empty");
        }
        // Additional title-specific validation can go here

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }
    }

    public void validateAuthor(String author) {
        List<String> errors = new ArrayList<>();
        if (author == null || author.isBlank()) {
            errors.add("Author cannot be null or empty");
        }
        if (author.length() < 3) {
            errors.add("Author must be at least 3 characters");
        }
        // Additional author-specific validation can go here

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }
    }

    public void validatePublishedDate(LocalDateTime date) {
        List<String> errors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        if (date == null) {
            errors.add("Date cannot be null");
        }
        if (date.isAfter(now)) {
            errors.add("Date cannot be in the future");
        }
        // Additional date-specific validation can go here

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }
    }

    public void validateDesignation(String designation) {
        List<String> errors = new ArrayList<>();
        if (designation == null || designation.isBlank()) {
            errors.add("Designation cannot be null or empty");
        }
    }
}
