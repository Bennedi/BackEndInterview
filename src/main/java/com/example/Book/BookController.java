package com.example.Book;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //Paging and Sorting
    @GetMapping("/")
    @Cacheable(value="booksCache")
    public Page<Books> getAllBooks (
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sort
            )
    {
        return bookService.getBookPagination(pageNumber,pageSize,sort);
    }

    //ExportToCSVFilw

    @PostMapping("/exportBooksToCsv")
    public String exportBooksToCsv (){
        return bookService.exportBooksToCsv();
    }


    @GetMapping("/search/{search}")
    public List<Books> getBooksByTitleOrAuthor (@PathVariable String search){
        return bookService.findBooksByTitleOrAuthor(search);
    }

    @GetMapping("/{pageNumber}/{pageSize}/{sortType}")
    public Page<Books> getAllBooksBySortType  (
            @PathVariable int pageNumber,
            @PathVariable int pageSize,
            @PathVariable String sortType
    )
    {
        return bookService.getBookPagination(pageNumber,pageSize,sortType);
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
    @CacheEvict(value = "booksCache", allEntries = true)
    public Books createBook(@RequestBody Books books) {
        return bookService.addBook(books);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    @CacheEvict(value = "booksCache", allEntries = true)
    public Books updateBook(@PathVariable Long id, @RequestBody Books books) {
        return bookService.updateBook(id, books);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @CacheEvict(value = "booksCache", allEntries = true)
    public void deleteBook(@PathVariable int id) {
        bookService.deleteBookById((long) id);
    }


}
