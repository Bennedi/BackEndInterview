package com.example.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final FieldValidator fieldValidator;

    @Autowired
    public BookService(BookRepository bookRepository, FieldValidator fieldValidator) {
        this.bookRepository = bookRepository;
        this.fieldValidator = fieldValidator;
    }

    public List<Books> getAllBooks() {
        List<Books> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new CustomException(Arrays.asList("No books found"));
        }
        return books;
    }

    public List<Books> getBookById(Long id) {
        return bookRepository.findById(Math.toIntExact(id));
    }

    public Books addBook(Books book) {
        if (bookRepository.existsById(Math.toIntExact(book.getId()))) {
            throw new CustomException(Arrays.asList("Book already exists"));
        }

        // Collect validation errors
        List<String> errors = new ArrayList<>();
        try {
            fieldValidator.validateTitle(book.getTitle());
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        try {
            fieldValidator.validateAuthor(book.getAuthor());
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        try {
            fieldValidator.validatePublishedDate(book.getPublishedDated());
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }

        return bookRepository.save(book);
    }

    public void deleteBookById(Long id) {
        if (!bookRepository.existsById(Math.toIntExact(id))) {
            throw new CustomException(Arrays.asList("Book not found"));
        }
        bookRepository.deleteById(Math.toIntExact(id));
    }

    public Books updateBook(Long id, Books updatedBook) {
        Books existingBook = (Books) bookRepository.findById(Math.toIntExact(id));

        // Collect validation errors
        List<String> errors = new ArrayList<>();
        try {
            if (updatedBook.getTitle() != null) {
                fieldValidator.validateTitle(updatedBook.getTitle());
                existingBook.setTitle(updatedBook.getTitle());
            }
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        try {
            if (updatedBook.getAuthor() != null) {
                fieldValidator.validateAuthor(updatedBook.getAuthor());
                existingBook.setAuthor(updatedBook.getAuthor());
            }
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        try {
            if (updatedBook.getPublishedDated() != null) {
                fieldValidator.validatePublishedDate(updatedBook.getPublishedDated());
                existingBook.setPublishedDated(updatedBook.getPublishedDated());
            }
        } catch (CustomException e) {
            errors.addAll(e.getErrors());
        }

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }

        return bookRepository.save(existingBook);
    }
    public Books getBooksByDesignation(String designation) {
        return bookRepository.findTopByDesignationOrderByTitleDesc(designation);
    }

    public Page<Books> getBookPagination(int pageNumber, int pageSize, String sortProperty) {
        Pageable pageable = null;
        if(null!=sortProperty){
            pageable = PageRequest.of(pageNumber-1, pageSize, Sort.Direction.ASC,sortProperty);
        }else{
            pageable = PageRequest.of(pageNumber-1, pageSize, Sort.by("id"));
        }
        return bookRepository.findAll(pageable);

    }
    public List<Books> deleteBookByDesignation(String designation) {
        return bookRepository.deleteByDesignation(designation);
    }

}
