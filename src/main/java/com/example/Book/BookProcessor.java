package com.example.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookProcessor implements ItemProcessor<Book, Book> {
    private static final Logger log = LoggerFactory.getLogger(BookProcessor.class);

    @Override

    public Book process(final Book item) throws Exception {
        final String title = item.title().toUpperCase();
        final String author = item.author().toUpperCase();
        final LocalDateTime publishedDated = item.publishedDated();
        final String designation = item.designation().toUpperCase();

        final Book transformedBook = new Book(title,author,publishedDated,designation);

        log.info("Converting ("+item+")into("+transformedBook+")");
        return transformedBook;
    }
}
