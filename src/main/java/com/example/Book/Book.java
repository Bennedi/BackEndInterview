package com.example.Book;

import java.time.LocalDateTime;

public record Book(String title, String author, LocalDateTime publishedDated, String designation) {

}
