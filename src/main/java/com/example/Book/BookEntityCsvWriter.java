package com.example.Book;

import java.io.File;
import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

public abstract class BookEntityCsvWriter implements ItemWriter<Book> {
    private static final String CSV_FILE = "output.csv";
    private FlatFileItemWriter<Book> writer;

    public BookEntityCsvWriter()
    {
        initializeCsvFile();
        this.writer = new FlatFileItemWriter<>();
        this.writer.setResource(
                new FileSystemResource(CSV_FILE));
        this.writer.setLineAggregator(
                new DelimitedLineAggregator<Book>() {
                    {
                        setDelimiter(",");
                        setFieldExtractor(
                                new BeanWrapperFieldExtractor<
                                        Book>() {
                                    {
                                        setNames(new String[] {
                                                "id", "author", "name",
                                                "price" });
                                    }
                                });
                    }
                });
    }

    private void initializeCsvFile()
    {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {
                throw new RuntimeException(
                        "Error creating CSV file", e);
            }
        }
    }

    public void write(List<? extends Book> items)
            throws Exception
    {


    }
}