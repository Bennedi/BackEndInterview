package com.example.Book;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final FieldValidator fieldValidator;

    @Autowired
    public BookService(BookRepository bookRepository, FieldValidator fieldValidator) {
        this.bookRepository = bookRepository;
        this.fieldValidator = fieldValidator;
    }

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Job job;

    public List<Books> findBooksByTitleOrAuthor(String search) {
        List<Books> books = bookRepository.findAll();

        if (books.isEmpty()) {
            throw new CustomException(List.of("No books found in the repository"));
        }

        // Filter books based on title or author
        List<Books> filteredBooks = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(search.toLowerCase())
                        || book.getAuthor().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        // Check if any books matched the search criteria
        if (filteredBooks.isEmpty()) {
            throw new CustomException(List.of("No books found matching the search criteria"));
        }

        return filteredBooks;
    }




    public List<Books> getAllBooks() {
        List<Books> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new CustomException(List.of("No books found"));
        }
        return books;
    }

    public List<Books> getBookById(Long id) {
        return bookRepository.findById(Math.toIntExact(id));
    }

    public Books addBook(Books book) {
        if (bookRepository.existsById(Math.toIntExact(book.getId()))) {
            throw new CustomException(List.of("Book already exists"));
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
        try {
            fieldValidator.validateDesignation(book.getDesignation());
        }catch (CustomException e){
            errors.addAll(e.getErrors());
        }
        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }

        return bookRepository.save(book);
    }

    public void deleteBookById(Long id) {
        if (!bookRepository.existsById(Math.toIntExact(id))) {
            throw new CustomException(List.of("Book not found"));
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
        Pageable pageable;
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

    public String exportBooksToCsv() {
        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter("books.csv"))) {
            // Jalankan job batch
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                throw new RuntimeException("Spring Batch job failed to complete.");
            }

            // Setelah job selesai, ekspor data ke file CSV
            List<Book> books = jdbcTemplate.query(
                    "SELECT title, author, published_dated, designation FROM books",
                    new DataClassRowMapper<>(Book.class)
            );

            // Tulis header CSV
            csvWriter.write("Title,Author,Published Date,Designation\n");

            // Tulis data buku ke CSV
            for (Book book : books) {
                csvWriter.write(String.format("%s,%s,%s,%s\n",
                        book.title(), book.author(), book.publishedDated(), book.designation()));
            }

            return "Books exported to CSV successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to export books to CSV: " + e.getMessage();
        }
    }
}
