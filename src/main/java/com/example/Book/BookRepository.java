package com.example.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Books, Integer> {
    List<Books> findById(int id);

    Books findTopByDesignationOrderByTitleDesc(String designation);

    List<Books> deleteByDesignation(String designation);
}
