package com.example.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    //Paging and Sorting
    @RequestMapping("/")
    public Page<Books> getAllBooks (
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sort
            )
    {
        return bookService.getBookPagination(pageNumber,pageSize,sort);
    }

    @RequestMapping("/{pageNumber}/{pageSize}/sortType")
    public Page<Books> getAllBooksBySortType  (
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam String sort
    )
    {
        return bookService.getBookPagination(pageNumber,pageSize,sort);
    }

    @GetMapping("/genre/{designation}")
    public Books getBookByDesignation(@PathVariable String designation) {
        return bookService.getBooksByDesignation(designation);
    }
    @DeleteMapping("/deletedGenre/{designation}")
    public List<Books> deleteBookByDesignation(@PathVariable String designation) {
        return bookService.deleteBookByDesignation(designation);
    }

    @GetMapping("")
    public List<Books> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public List<Books> getBook(@PathVariable int id) {
        return bookService.getBookById((long) id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Books createBook(@RequestBody Books books) {
        return bookService.addBook(books);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public Books updateBook(@PathVariable Long id, @RequestBody Books books) {
        return bookService.updateBook(id, books);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id) {
        bookService.deleteBookById((long) id);
    }


}
